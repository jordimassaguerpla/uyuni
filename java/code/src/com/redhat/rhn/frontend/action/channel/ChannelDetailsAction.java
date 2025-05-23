/*
 * Copyright (c) 2009--2017 Red Hat, Inc.
 *
 * This software is licensed to you under the GNU General Public License,
 * version 2 (GPLv2). There is NO WARRANTY for this software, express or
 * implied, including the implied warranties of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. You should have received a copy of GPLv2
 * along with this software; if not, see
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt.
 *
 * Red Hat trademarks are not licensed under GPLv2. No permission is
 * granted to use or replicate Red Hat trademarks that are incorporated
 * in this software or its documentation.
 */
package com.redhat.rhn.frontend.action.channel;

import com.redhat.rhn.common.hibernate.HibernateFactory;
import com.redhat.rhn.common.localization.LocalizationService;
import com.redhat.rhn.domain.access.AccessGroupFactory;
import com.redhat.rhn.domain.channel.Channel;
import com.redhat.rhn.domain.channel.ChannelFactory;
import com.redhat.rhn.domain.server.ServerFactory;
import com.redhat.rhn.domain.user.User;
import com.redhat.rhn.frontend.struts.RequestContext;
import com.redhat.rhn.frontend.struts.RhnAction;
import com.redhat.rhn.frontend.struts.RhnHelper;
import com.redhat.rhn.manager.channel.ChannelManager;
import com.redhat.rhn.manager.system.SystemManager;
import com.redhat.rhn.manager.user.UserManager;

import com.suse.manager.webui.services.pillar.MinionPillarManager;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ChannelDetailsAction handles the interaction of the ChannelDetails page.
 */
public class ChannelDetailsAction extends RhnAction {

    /** {@inheritDoc} */
    @Override
    public ActionForward execute(ActionMapping mapping,
            ActionForm formIn,
            HttpServletRequest request,
            HttpServletResponse response) {

        DynaActionForm form = (DynaActionForm)formIn;
        RequestContext ctx = new RequestContext(request);
        User user =  ctx.getCurrentUser();
        Map<String, Object> params = makeParamMap(request);
        String fwd = RhnHelper.DEFAULT_FORWARD;

        long cid = ctx.getRequiredParam("cid");
        Channel chan = ChannelManager.lookupByIdAndUser(cid, user);

        if (isSubmitted(form) && (
                (chan.getOrg() == null && user.isMemberOf(AccessGroupFactory.CHANNEL_ADMIN)) ||
                    UserManager.verifyChannelAdmin(user, chan))) {
            String global = (String)form.get("global");
            chan.setGloballySubscribable((global != null) &&
                    ("all".equals(global)), user.getOrg());

            chan.setGPGCheck(BooleanUtils.isTrue((Boolean)form.get("gpg_check")));


            createSuccessMessage(request, "message.channelupdated",
                    chan.getName());

            //did they enable per user subscriptions?
            if (!global.equals("all")) {
                addMessage(request, "message.channelsubscribers");
            }

            // this is evil but necessary
            chan = HibernateFactory.reload(chan);
            params.put("cid", cid);
            fwd = "success";
            ServerFactory.listMinionsByChannel(cid).stream()
                    .forEach(ms -> MinionPillarManager.INSTANCE.generatePillar(ms, false, Collections.emptySet()));
        }

        request.setAttribute("systems_subscribed",
                SystemManager.subscribedToChannelSize(user, cid));
        request.setAttribute("channel_name", chan.getName());
        request.setAttribute("pack_size", ChannelFactory.getPackageCount(chan));
        request.setAttribute("globally", chan.isGloballySubscribable(user.getOrg()));
        request.setAttribute("channel", chan);
        request.setAttribute("channel_last_modified", LocalizationService.
                    getInstance().formatCustomDate(chan.getLastModified()));
        //Check if the channel needed repodata,
        // if so get the status and last build info
        if (chan.isChannelRepodataRequired()) {
            request.setAttribute("repo_status",
                    ChannelManager.isChannelLabelInProgress(chan.getLabel()));
            request.setAttribute("repo_last_build", ChannelManager.getRepoLastBuild(chan));
        }
        // turn on the right radio button
        if (chan.isGloballySubscribable(user.getOrg())) {
            form.set("global", "all");
        }
        else {
            form.set("global", "selected");
        }

        request.setAttribute("gpg_check", chan.isGPGCheck());

        if ((chan.getOrg() == null && user.isMemberOf(AccessGroupFactory.CHANNEL_ADMIN)) ||
                UserManager.verifyChannelAdmin(user, chan)) {
            request.setAttribute("has_access", true);
        }
        else {
            request.setAttribute("has_access", false);
        }

        return getStrutsDelegate().forwardParams(
                mapping.findForward(fwd), params);
    }
}
