/**
 * Copyright (C) 2011 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 */
package org.bonitasoft.engine.authentication;

import java.io.Serializable;
import java.util.Map;


/**
 * @author Elias Ricken de Medeiros
 * @since 6.0
 */
public interface AuthenticationService {

    /**
     * Check user credentials by give user name and passwordHash
     * 
     * @param credentials
     *            the credentials elements to use to authenticate
     * @return true if user is authenticated
     * @throws AuthenticationException
     *             Error thrown if either the password is invalid or the user is not found.
     */
    boolean checkUserCredentials(Map<String, Serializable> credentials) throws AuthenticationException;

    /**
     * logout given user from the authentication service set up
     * 
     * @param credentials
     *            the credentials used to call authentication service
     * @throws AuthenticationException
     *             if a problem occurs during logout or if credentials are not valid
     */
    void logout(Map<String, Serializable> credentials) throws AuthenticationException;

}
