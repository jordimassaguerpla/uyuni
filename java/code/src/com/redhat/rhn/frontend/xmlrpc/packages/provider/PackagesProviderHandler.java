/*
 * Copyright (c) 2009--2014 Red Hat, Inc.
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
package com.redhat.rhn.frontend.xmlrpc.packages.provider;

import com.redhat.rhn.domain.rhnpackage.PackageFactory;
import com.redhat.rhn.domain.rhnpackage.PackageKey;
import com.redhat.rhn.domain.rhnpackage.PackageKeyType;
import com.redhat.rhn.domain.rhnpackage.PackageProvider;
import com.redhat.rhn.domain.role.RoleFactory;
import com.redhat.rhn.domain.user.User;
import com.redhat.rhn.frontend.xmlrpc.BaseHandler;
import com.redhat.rhn.frontend.xmlrpc.InvalidPackageKeyTypeException;
import com.redhat.rhn.frontend.xmlrpc.InvalidPackageProviderException;
import com.redhat.rhn.frontend.xmlrpc.PermissionCheckFailureException;

import com.suse.manager.api.ReadOnly;

import org.apache.commons.text.StringEscapeUtils;

import java.util.List;
import java.util.Set;

/**
 * PackagesProvider
 * @apidoc.namespace packages.provider
 * @apidoc.doc Methods to retrieve information about Package Providers associated with
 *      packages.
 */
public class PackagesProviderHandler extends BaseHandler {

    /**
     * list the package providers
     * @param loggedInUser The current user
     * @return List of package providers
     *
     * @apidoc.doc List all Package Providers.
     * User executing the request must be a #product() administrator.
     * @apidoc.param #session_key()
     * @apidoc.returntype
     *  #return_array_begin()
     *      $PackageProviderSerializer
     *  #array_end()
     */
    public List<PackageProvider> list(User loggedInUser) {
        isSatelliteAdmin(loggedInUser);
        return PackageFactory.listPackageProviders();
    }



    /**
     * List the keys associated with a package provider
     * @param loggedInUser The current user
     * @param providerName the provider name
     * @return set of package keys
     *
     * @apidoc.doc List all security keys associated with a package provider.
     * User executing the request must be a #product() administrator.
     * @apidoc.param #session_key()
     * @apidoc.param #param_desc("string", "providerName", "The provider name")
     * @apidoc.returntype
     *  #return_array_begin()
     *      $PackageKeySerializer
     *  #array_end()
     */
    @ReadOnly
    public Set<PackageKey> listKeys(User loggedInUser, String providerName) {
        isSatelliteAdmin(loggedInUser);
        PackageProvider prov = PackageFactory.lookupPackageProvider(providerName);
        if (prov == null) {
            throw new InvalidPackageProviderException(providerName);
        }
        return prov.getKeys();

    }

    /**
     * Associate a package key with provider.  Provider is created if it doesn't exist.
     *  Key is created if it doesn't exist.
     * @param loggedInUser The current user
     * @param providerName the provider name
     * @param key the key string
     * @param type the type string (currently only 'gpg' is supported)
     * @return 1 on success
     *
     * @apidoc.doc Associate a package security key and with the package provider.
     *      If the provider or key doesn't exist, it is created. User executing the
     *      request must be a #product() administrator.
     * @apidoc.param #session_key()
     * @apidoc.param #param_desc("string", "providerName", "The provider name")
     * @apidoc.param #param_desc("string", "key", "The actual key")
     * @apidoc.param #param_desc("string", "type", "The type of the key. Currently,
     * only 'gpg' is supported")
     * @apidoc.returntype
     *      #return_int_success()
     */
    public int associateKey(User loggedInUser, String providerName, String key,
            String type) {
        isSatelliteAdmin(loggedInUser);
        PackageProvider prov = PackageFactory.lookupPackageProvider(providerName);
        if (prov == null) {
            prov = new PackageProvider();
            prov.setName(providerName);
        }

        //package key type might be invalid
        PackageKeyType keyType = PackageFactory.lookupKeyTypeByLabel(type);
        if (keyType == null) {
            throw new InvalidPackageKeyTypeException(type);
        }


        PackageKey pKey = PackageFactory.lookupPackageKey(key);
        if (pKey == null) {
            pKey = new PackageKey();
            pKey.setKey(StringEscapeUtils.escapeHtml4(key));
            pKey.setType(keyType);
        }

        pKey.setProvider(prov);
        prov.addKey(pKey);

        PackageFactory.save(prov);

        return 1;
    }



    private void isSatelliteAdmin(User user) {
        if (!user.hasRole(RoleFactory.SAT_ADMIN)) {
            throw new PermissionCheckFailureException(RoleFactory.SAT_ADMIN);
        }
    }

}
