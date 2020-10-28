alter type evr_t add attribute type varchar(10);

ALTER TABLE rhnpackageevr DISABLE TRIGGER USER

select pe.id, array_agg(distinct pa.label like '%-deb') from rhnpackagearch pa
             join rhnpackage p on p.package_arch_id = pa.id
             join rhnpackageevr pe on p.evr_id = pe.id
             group by pe.id;

update rhnpackageevr set evr.type = 'rpm'

ALTER TABLE rhnpackageevr ENABLE TRIGGER USER

-- update evr_t comparison function to take type into account.
create or replace function evr_t_compare( a evr_t, b evr_t )
returns int as $$
begin
  if a.type = b.type then
      if a.type = 'rpm' then
        return rpm.vercmp(a.epoch, a.version, a.release, b.epoch, b.version, b.release);
      elsif a.type = 'deb' then
        return deb.debvercmp(a.epoch, a.version, a.release, b.epoch, b.version, b.release);
      else
        raise notice 'unknown evr type';
      end if;
  else
     raise notice 'cant compare incompatible evr types';
  end if;
end;
$$ language plpgsql immutable strict;




create or replace function
insert_evr(e_in in varchar, v_in in varchar, r_in in varchar, t_in in varchar)
returns numeric
as
$$
declare
    evr_id  numeric;
begin
    evr_id := nextval('rhn_pkg_evr_seq');

    insert into rhnPackageEVR(id, epoch, version, release, evr)
        values (evr_id, e_in, v_in, r_in, evr_t(e_in, v_in, r_in, t_in))
        on conflict do nothing;

    select id
        into strict evr_id
        from rhnPackageEVR
        where ((epoch is null and e_in is null) or (epoch = e_in)) and
           version = v_in and release = r_in and type = t_in;

    return evr_id;
end;
$$ language plpgsql;


create or replace function
lookup_evr2(e_in in varchar, v_in in varchar, r_in in varchar, t_in in varchar)
returns numeric
as
$$
declare
    evr_id  numeric;
begin
    select id
      into evr_id
      from rhnPackageEVR
     where ((epoch is null and e_in is null) or (epoch = e_in)) and
           version = v_in and
           release = r_in and
           type = t_in;

    if not found then
        -- HACK: insert is isolated in own function in order to be able to declare this function immutable
        -- Postgres optimizes immutable functions calls but those are compatible with the contract of lookup_\*
        -- see https://www.postgresql.org/docs/9.6/xfunc-volatility.html
        return insert_evr(e_in, v_in, r_in, t_in);
    end if;

    return evr_id;
end;
$$ language plpgsql immutable;
