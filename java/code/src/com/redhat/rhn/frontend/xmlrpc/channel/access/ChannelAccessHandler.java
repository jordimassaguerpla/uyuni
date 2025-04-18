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
package com.redhat.rhn.frontend.xmlrpc.channel.access;

import com.redhat.rhn.FaultException;
import com.redhat.rhn.domain.access.AccessGroupFactory;
import com.redhat.rhn.domain.channel.Channel;
import com.redhat.rhn.domain.channel.ChannelFactory;
import com.redhat.rhn.domain.channel.InvalidChannelRoleException;
import com.redhat.rhn.domain.user.User;
import com.redhat.rhn.frontend.xmlrpc.BaseHandler;
import com.redhat.rhn.frontend.xmlrpc.InvalidAccessValueException;
import com.redhat.rhn.frontend.xmlrpc.NoSuchChannelException;
import com.redhat.rhn.frontend.xmlrpc.PermissionCheckFailureException;
import com.redhat.rhn.manager.channel.ChannelManager;

import com.suse.manager.api.ReadOnly;

/**
 * ChannelAccessHandler
 * @apidoc.namespace channel.access
 * @apidoc.doc Provides methods to retrieve and alter channel access restrictions.
 */
public class ChannelAccessHandler extends BaseHandler {

    /**
     * Enable user restrictions for the given channel. If enabled, only
     * selected users within the organization may subscribe to the channel.
     * @param loggedInUser The current user
     * @param channelLabel The label for the channel to change
     * @return Returns 1 if successful, exception otherwise
     * @throws FaultException A FaultException is thrown if:
     *   - The sessionkey is invalid
     *   - The channel label is invalid
     *   - The user doesn't have channel admin permissions
     *
     * @apidoc.doc Enable user restrictions for the given channel. If enabled, only
     * selected users within the organization may subscribe to the channel.
     * @apidoc.param #session_key()
     * @apidoc.param #param_desc("string", "channelLabel", "label of the channel")
     * @apidoc.returntype  #return_int_success()
     */
    public int enableUserRestrictions(User loggedInUser, String channelLabel)
        throws FaultException {

        Channel channel = lookupChannelByLabel(loggedInUser, channelLabel);
        // This can be set for null-org channels by channel admin
        if ((channel.getOrg() != null) || (!loggedInUser.isMemberOf(AccessGroupFactory.CHANNEL_ADMIN))) {
            verifyChannelAdmin(loggedInUser, channel);
        }

        channel.setGloballySubscribable(false, loggedInUser.getOrg());
        ChannelFactory.save(channel);

        return 1;
    }

    /**
     * Disable user restrictions for the given channel. If disabled,
     * all users within the organization may subscribe to the channel.
     * @param loggedInUser The current user
     * @param channelLabel The label for the channel to change
     * @return Returns 1 if successful, exception otherwise
     * @throws FaultException A FaultException is thrown if:
     *   - The sessionkey is invalid
     *   - The channel label is invalid
     *   - The user doesn't have channel admin permissions
     *
     * @apidoc.doc Disable user restrictions for the given channel.  If disabled,
     * all users within the organization may subscribe to the channel.
     * @apidoc.param #session_key()
     * @apidoc.param #param_desc("string", "channelLabel", "label of the channel")
     * @apidoc.returntype  #return_int_success()
     */
    public int disableUserRestrictions(User loggedInUser, String channelLabel)
        throws FaultException {

        Channel channel = lookupChannelByLabel(loggedInUser, channelLabel);
        // This can be set for null-org channels by channel admin
        if ((channel.getOrg() != null) || (!loggedInUser.isMemberOf(AccessGroupFactory.CHANNEL_ADMIN))) {
            verifyChannelAdmin(loggedInUser, channel);
        }

        channel.setGloballySubscribable(true, loggedInUser.getOrg());
        ChannelFactory.save(channel);

        return 1;
    }

    /**
     * Set organization sharing access control.
     * @param loggedInUser The current user
     * @param channelLabel The label for the channel to change
     * @param access The access value to set. (Must be one of the following:
     * "public", "private" or "protected")
     * @return Returns 1 if successful, exception otherwise
     * @throws FaultException A FaultException is thrown if:
     *   - The sessionKey is invalid
     *   - The channelLabel is invalid
     *   - The access is invalid
     *   - The user doesn't have channel admin permissions
     *
     * @apidoc.doc Set organization sharing access control.
     * @apidoc.param #session_key()
     * @apidoc.param #param_desc("string", "channelLabel", "label of the channel")
     * @apidoc.param #param_desc("string", "access", "Access (one of the
     *                  following: 'public', 'private', or 'protected'")
     * @apidoc.returntype  #return_int_success()
     */
    public int setOrgSharing(User loggedInUser, String channelLabel, String access)
        throws FaultException {

        Channel channel = lookupChannelByLabel(loggedInUser, channelLabel);
        verifyChannelAdmin(loggedInUser, channel);

        if (channel.isValidAccess(access)) {
            channel.setAccess(access);
            ChannelFactory.save(channel);
        }
        else {
            throw new InvalidAccessValueException(access);
        }
        return 1;
    }

    /**
     * Get organization sharing access control.
     * @param loggedInUser The current user
     * @param channelLabel The label for the channel
     * @return The access value
     * @throws FaultException A FaultException is thrown if:
     *   - The sessionKey is invalid
     *   - The channelLabel is invalid
     *   - The access is invalid
     *   - The user doesn't have channel admin permissions
     *
     * @apidoc.doc Get organization sharing access control.
     * @apidoc.param #session_key()
     * @apidoc.param #param_desc("string", "channelLabel", "label of the channel")
     * @apidoc.returntype
     *  #param_desc("string", "access", "The access value (one of the following: 'public', 'private', or 'protected'")
     */
    @ReadOnly
    public String getOrgSharing(User loggedInUser, String channelLabel)
        throws FaultException {

        Channel channel = lookupChannelByLabel(loggedInUser, channelLabel);
        verifyChannelAdmin(loggedInUser, channel);

        return channel.getAccess();
    }

    private Channel lookupChannelByLabel(User user, String label) throws NoSuchChannelException {
        Channel channel = ChannelFactory.lookupByLabelAndUser(label, user);
        if (channel == null) {
            throw new NoSuchChannelException();
        }

        return channel;
    }

    private void verifyChannelAdmin(User user, Channel channel) {
        try {
            ChannelManager.verifyChannelAdmin(user, channel.getId());
        }
        catch (InvalidChannelRoleException e) {
            throw new PermissionCheckFailureException();
        }
    }
}
