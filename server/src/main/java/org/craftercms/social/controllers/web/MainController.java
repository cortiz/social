/*
 * Copyright (C) 2007-2014 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.social.controllers.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.craftercms.commons.http.HttpUtils;
import org.craftercms.profile.api.Profile;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

/**
 * Controller for the main view.
 *
 * @author avasquez
 */
@Controller
@RequestMapping("/")
public class MainController {

    public static final String VIEW_MAIN = "main";

    public static final String MODEL_LOGGED_IN_USER = "loggedInUser";
    public static final String MODEL_SOCIAL_APP_URL = "socialAppUrl";
    private static final String IS_LOGGED_USER_SUPERADMIN = "isSuperAdmin";

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView viewMain(Map<String, Object> model, HttpServletRequest request) {
        StringBuilder socialAppUrl;

        socialAppUrl = HttpUtils.getBaseRequestUrl(request, false).append("/");

        Profile loggedUser=getLoggedInUser(request);

        ModelAndView mav;
         mav = new ModelAndView(VIEW_MAIN);

        mav.addObject(MODEL_LOGGED_IN_USER, loggedUser);
        mav.addObject("requestContext", request);
        if (loggedUser!=null) {
            mav.addObject(IS_LOGGED_USER_SUPERADMIN, isSuperAdmin(loggedUser));
        }
        mav.addObject(MODEL_SOCIAL_APP_URL, socialAppUrl.toString());

        return mav;
    }

    private boolean isSuperAdmin(final Profile loggedUser) {
        return loggedUser.getRoles().contains("SOCIAL_SUPERADMIN");
    }

    private Profile getLoggedInUser(HttpServletRequest request) {
        Authentication auth = SecurityUtils.getAuthentication(request);
        if (auth != null) {
            return auth.getProfile();
        } else {
         return null;
        }
    }



}
