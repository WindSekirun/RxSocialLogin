package com.linkedin.platform.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the types of data for which access is being requested.
 */
public class Scope {

    /**
     * Your Profile Overview 	Name, photo, headline, and current positions
     */
    public static final LIPermission R_BASICPROFILE = new LIPermission("r_basicprofile", "Name, photo, headline and current position");

    /**
     * Your Full Profile 	Full profile including experience, education, skills, and recommendations
     */
    public static final LIPermission R_FULLPROFILE = new LIPermission("r_fullprofile", "Full profile including experience, education, skills and recommendations");

    /**
     * Your Email Address 	The primary email address you use for your LinkedIn account
     */
    public static final LIPermission R_EMAILADDRESS = new LIPermission("r_emailaddress", "Your email address");

    /**
     * Your Contact Info 	Address, phone number, and bound accounts
     */
    public static final LIPermission R_CONTACTINFO = new LIPermission("r_contactinfo", "Your contact info");

    /**
     * Company Page & Analytics 	Edit company pages for which I am an Admin and post status updates on behalf of those companies
     */
    public static final LIPermission RW_COMPANY_ADMIN = new LIPermission("rw_company_admin", "Manage your company page and post updates");

    /**
     * Share, comment & like    Post updates, make comments and like posts
     */
    public static final LIPermission W_SHARE = new LIPermission("w_share", "Post updates, make comments and like posts as you");


    private Set<LIPermission> permissions = new HashSet<LIPermission>();

    /**
     * build a Scope with the list of desired permissions
     * @param permissions
     * @return
     */
    public static synchronized Scope build(LIPermission... permissions) {
        return new Scope(permissions);
    }

    private Scope(LIPermission... permissions) {
        if (permissions == null) {
            return;
        }
        for (LIPermission perm : permissions) {
            this.permissions.add(perm);
        }
    }

    public String createScope() {
        return join(" ", permissions);
    }

    private static String join(CharSequence delimiter, Collection<LIPermission> tokens) {
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;
        for (LIPermission token: tokens) {
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(delimiter);
            }
            sb.append(token.name);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return createScope();
    }

    public static class LIPermission {
        private final String name;
        private final String description;

        public LIPermission(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

}
