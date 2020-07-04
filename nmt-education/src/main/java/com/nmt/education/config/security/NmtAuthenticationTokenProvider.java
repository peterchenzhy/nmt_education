package com.nmt.education.config.security;

import com.nmt.education.commmons.Consts;
import com.nmt.education.commmons.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

@Slf4j
public class NmtAuthenticationTokenProvider implements AuthenticationProvider {

    /**
     * Performs authentication with the same contract as
     * {@link AuthenticationManager#authenticate(Authentication)}
     * .
     *
     * @param authentication the authentication request object.
     * @return a fully authenticated object including credentials. May return
     * <code>null</code> if the <code>AuthenticationProvider</code> is unable to support
     * authentication of the passed <code>Authentication</code> object. In such a case,
     * the next <code>AuthenticationProvider</code> that supports the presented
     * <code>Authentication</code> class will be tried.
     * @throws AuthenticationException if authentication fails.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        NmtAuthenticationToken authenticationToken = (NmtAuthenticationToken) authentication;
        if (Consts.ROLE_ROOT.equals(authenticationToken.getCredentials())) {
            return authentication;
        }

        TokenUtil.Token t = null;
        try {
            t = TokenUtil.verifyToken(authenticationToken.getToken());
        } catch (Exception e) {
            log.error("token解析异常，token: ", e);
            throw new AuthenticationServiceException("token解析失败");
        }
        try {
            Assert.isTrue(authenticationToken.getPrincipal().equals(t.getLoginUserId()), "token异常loginUserId");
            Assert.isTrue(authenticationToken.getCredentials().equals(t.getRoleId()), "token异常roleId");
        } catch (Exception ex) {
            throw new AuthenticationServiceException(ex.getMessage());
        }

        return authentication;
    }

    /**
     * Returns <code>true</code> if this <Code>AuthenticationProvider</code> supports the
     * indicated <Code>Authentication</code> object.
     * <p>
     * Returning <code>true</code> does not guarantee an
     * <code>AuthenticationProvider</code> will be able to authenticate the presented
     * instance of the <code>Authentication</code> class. It simply indicates it can
     * support closer evaluation of it. An <code>AuthenticationProvider</code> can still
     * return <code>null</code> from the {@link #authenticate(Authentication)} method to
     * indicate another <code>AuthenticationProvider</code> should be tried.
     * </p>
     * <p>
     * Selection of an <code>AuthenticationProvider</code> capable of performing
     * authentication is conducted at runtime the <code>ProviderManager</code>.
     * </p>
     *
     * @param authentication
     * @return <code>true</code> if the implementation can more closely evaluate the
     * <code>Authentication</code> class presented
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return (NmtAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
