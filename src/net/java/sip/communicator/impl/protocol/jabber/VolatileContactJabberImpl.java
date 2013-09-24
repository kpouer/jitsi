/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.protocol.jabber;

import net.java.sip.communicator.service.protocol.*;

import org.jivesoftware.smack.util.*;

/**
 * The Jabber implementation for Volatile Contact
 * @author Damian Minkov
 */
public class VolatileContactJabberImpl
    extends ContactJabberImpl
{
    /**
     * This contact id
     */
    private String contactId = null;
    
    /**
     * Indicates whether the contact is private messaging contact or not.
     */
    private boolean isPrivateMessagingContact = false;
    
    /**
     * The display name of the contact. This property is used only for private 
     * messaging contacts.
     */
    private String displayName = null;
    
    /**
     * Creates an Volatile JabberContactImpl with the specified id
     * @param id String the user id/address
     * @param ssclCallback a reference to the ServerStoredContactListImpl
     * instance that created us.
     */
    VolatileContactJabberImpl(String id,
                              ServerStoredContactListJabberImpl ssclCallback)
    {
        this(id, ssclCallback, false);
    }
    
    /**
     * Creates an Volatile JabberContactImpl with the specified id
     * @param id String the user id/address
     * @param ssclCallback a reference to the ServerStoredContactListImpl
     * instance that created us.
     * @param isPrivateMessagingContact if <tt>true</tt> this should be private 
     * messaging contact.
     */
    VolatileContactJabberImpl(String id,
                              ServerStoredContactListJabberImpl ssclCallback,
                              boolean isPrivateMessagingContact)
    {
        super(null, ssclCallback, false, false);
        
        this.isPrivateMessagingContact = isPrivateMessagingContact;
        
        if(this.isPrivateMessagingContact)
        {
            displayName = StringUtils.parseResource(id) + " from " + 
                StringUtils.parseBareAddress(id);
            this.contactId = id;
            setJid(id, true);
        }
        else
        {
            this.contactId = StringUtils.parseBareAddress(id);
            String resource = StringUtils.parseResource(id);
            if(resource != null)
            {
                setJid(id, false);
            }
        }

        
    }

    /**
     * Returns the Jabber Userid of this contact
     * @return the Jabber Userid of this contact
     */
    @Override
    public String getAddress()
    {
        return contactId;
    }

    /**
     * Returns a String that could be used by any user interacting modules for
     * referring to this contact. An alias is not necessarily unique but is
     * often more human readable than an address (or id).
     * @return a String that can be used for referring to this contact when
     * interacting with the user.
     */
    @Override
    public String getDisplayName()
    {
        return (isPrivateMessagingContact? displayName : contactId);
    }

    /**
     * Returns a string representation of this contact, containing most of its
     * representative details.
     *
     * @return  a string representation of this contact.
     */
    @Override
    public String toString()
    {
        StringBuffer buff =  new StringBuffer("VolatileJabberContact[ id=");
        buff.append(getAddress()).append("]");

        return buff.toString();
    }

    /**
     * Determines whether or not this contact group is being stored by the
     * server. Non persistent contact groups exist for the sole purpose of
     * containing non persistent contacts.
     * @return true if the contact group is persistent and false otherwise.
     */
    @Override
    public boolean isPersistent()
    {
        return false;
    }
    
    /**
     * Updates the resources for this contact.
     * 
     * @param removeUnavailable whether to remove unavailable resources.
     */
    protected void updateResources(boolean removeUnavailable)
    {
        if(!isPrivateMessagingContact)
        {
            super.updateResources(removeUnavailable);
        }
    }

    /**
     * Checks if the contact is private messaging contact or not.
     * 
     * @return <tt>true</tt> if this is private messaging contact and 
     * <tt>false</tt> if it isn't.
     */
    public boolean isPrivateMessagingContact()
    {
        return isPrivateMessagingContact;
    }
    
    /**
     * Returns the real address of the contact. If the contact is not private 
     * messaging contact the result will be the same as <tt>getAddress</tt>'s 
     * result.
     * 
     * @return the real address of the contact.
     */
    @Override 
    public String getPersistableAddress()
    {
        if(!isPrivateMessagingContact)
            return getAddress();
        ChatRoomMemberJabberImpl chatRoomMember 
            = ((OperationSetMultiUserChatJabberImpl)getProtocolProvider()
                    .getOperationSet(OperationSetMultiUserChat.class))
                    .getChatRoom(StringUtils.parseBareAddress(contactId))
                    .findMemberForNickName(
                        StringUtils.parseResource(contactId));
        return ((chatRoomMember == null)? null : StringUtils.parseBareAddress(
            chatRoomMember.getJabberID()));
    }
   
}
