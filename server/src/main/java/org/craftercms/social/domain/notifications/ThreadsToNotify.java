/*
 * Copyright (C) 2007-2014 Crafter Software Corporation.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.social.domain.notifications;

import java.util.List;

import org.jongo.marshall.jackson.oid.MongoId;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 */
public class ThreadsToNotify {

    @MongoId
    @JsonProperty("_id")
    private String threadId;
    private List<String> profiles;

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(final String threadId) {
        this.threadId = threadId;
    }

    public List<String> getProfiles() {
        return profiles;
    }

    public void setProfiles(final List<String> profiles) {
        this.profiles = profiles;
    }
}
