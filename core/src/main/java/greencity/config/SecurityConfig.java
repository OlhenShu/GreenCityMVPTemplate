package greencity.config;

import greencity.security.filters.AccessTokenAuthenticationFilter;
import greencity.security.jwt.JwtTool;
import greencity.security.providers.JwtAuthenticationProvider;
import greencity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

import static greencity.constant.AppConstant.*;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

/**
 * Config for security.
 *
 * @author Nazar Stasyuk && Yurii Koval
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String ECONEWS_COMMENTS = "/econews/comments";
    private static final String USER_CUSTOM_SHOPPING_LIST_ITEMS = "/user/{userId}/custom-shopping-list-items";
    private static final String CUSTOM_SHOPPING_LIST = "/custom/shopping-list-items/{userId}";
    private static final String CUSTOM_SHOPPING_LIST_URL = "/custom/shopping-list-items/{userId}/"
        + "custom-shopping-list-items";
    private static final String CUSTOM_SHOPPING_LIST_ITEMS = "/{userId}/custom-shopping-list-items";
    private static final String HABIT_ASSIGN_ID = "/habit/assign/{habitId}";
    private static final String USER_SHOPPING_LIST = "/user/shopping-list-items";

    private final JwtTool jwtTool;
    private final UserService userService;

    /**
     * Constructor.
     */

    @Autowired
    public SecurityConfig(JwtTool jwtTool, UserService userService) {
        this.jwtTool = jwtTool;
        this.userService = userService;
    }

    /**
     * Bean {@link PasswordEncoder} that uses in coding password.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Method for configure security.
     *
     * @param http {@link HttpSecurity}
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
            .and()
            .csrf()
            .disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(
                new AccessTokenAuthenticationFilter(jwtTool, authenticationManager(), userService),
                UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling()
            .authenticationEntryPoint((req, resp, exc) -> resp.sendError(SC_UNAUTHORIZED, "Authorize first."))
            .accessDeniedHandler((req, resp, exc) -> resp.sendError(SC_FORBIDDEN, "You don't have authorities."))
            .and()
            .authorizeRequests()
            .antMatchers("/", "/management/", "/management/login").permitAll()
            .antMatchers("/management/**",
                "/econews/comments/replies/{parentCommentId}")
            .hasAnyRole(ADMIN)
            .antMatchers("/css/**",
                "/img/**")
            .permitAll()
            .antMatchers(HttpMethod.GET,
                ECONEWS_COMMENTS)
            .hasAnyRole(ADMIN)
            .antMatchers(HttpMethod.GET,
                "/ownSecurity/verifyEmail",
                "/ownSecurity/updateAccessToken",
                "/ownSecurity/restorePassword",
                "/factoftheday/",
                "/factoftheday/all",
                "/factoftheday/find",
                "/factoftheday/languages",
                "/category",
                "/habit",
                "/habit/{id}",
                "/habit/{id}/shopping-list",
                "/tags/search",
                "/tags/v2/search",
                "/habit/tags/all",
                "/habit/statistic/{habitId}",
                "/habit/statistic/assign/{habitAssignId}",
                "/habit/statistic/todayStatisticsForAllHabitItems",
                "/specification",
                "/econews",
                "/econews/newest",
                "/econews/tags",
                "/econews/tags/all",
                "/econews/recommended",
                "/econews/{id:[0-9]+}",
                "/econews/countLikes/{econewsId}",
                "/econews/comments/count/comments/{ecoNewsId}",
                "/econews/comments/count/replies/{parentCommentId}",
                "/econews/comments/count/likes",
                "/econews/comments/replies/active/{parentCommentId}",
                "/econews/comments/active",
                "/language",
                "/search",
                "/search/econews",
                "/user/emailNotifications",
                "/user/activatedUsersAmount",
                "/user/{userId}/habit/assign",
                "/token",
                "/search/events")
            .permitAll()
            .antMatchers(HttpMethod.POST,
                "/ownSecurity/signUp",
                "/ownSecurity/signIn",
                "/ownSecurity/changePassword",
                "/newsSubscriber")
            .permitAll()
            .antMatchers(HttpMethod.GET,
                "/achievements",
                CUSTOM_SHOPPING_LIST_ITEMS,
                CUSTOM_SHOPPING_LIST,
                CUSTOM_SHOPPING_LIST_URL,
                "/custom/shopping-list-items/{userId}/{habitId}",
                "/econews/count",
                "/econews/isLikedByUser",
                "/shopping-list-items",
                "/habit/assign/allForCurrentUser",
                "/habit/assign/active/{date}",
                "/habit/assign/{habitAssignId}/more",
                "/habit/assign/activity/{from}/to/{to}",
                HABIT_ASSIGN_ID + "/active",
                HABIT_ASSIGN_ID,
                HABIT_ASSIGN_ID + "/all",
                "/habit/statistic/acquired/count",
                "/habit/statistic/in-progress/count",
                "/facts",
                "/facts/random/{habitId}",
                "/facts/dayFact/{languageId}",
                "/newsSubscriber/unsubscribe",
                "/social-networks/image",
                "/user",
                "/user/shopping-list-items/habits/{habitId}/shopping-list",
                USER_CUSTOM_SHOPPING_LIST_ITEMS,
                "/user/{userId}/custom-shopping-list-items/available",
                "/user/{userId}/profile/",
                "/user/isOnline/{userId}/",
                "/user/{userId}/profileStatistics/",
                "/factoftheday/",
                "/factoftheday/all",
                "/user/shopping-list-items/{userId}/get-all-inprogress",
                "/habit/assign/{habitAssignId}/allUserAndCustomList",
                "/habit/assign/allUserAndCustomShoppingListsInprogress",
                "/habit/assign/{habitAssignId}",
                "/habit/tags/search",
                "/habit/search",
                "/habit/{habitId}/friends/profile-pictures",
                "/notifications/latest",
                "/friends",
                "/friends/not-friends-yet",
                "/friends/all",
                "/habit/{habitId}/friends/profile-pictures",
                "/notifications/latest",
                "/friends/recommended,",
                "/events/count")
            .hasAnyRole(USER, ADMIN, MODERATOR, UBS_EMPLOYEE)
            .antMatchers(HttpMethod.POST,
                    "/events/create",
                "/category",
                "/econews",
                "/econews/like",
                "/econews/dislike",
                "/econews/comments/{econewsId}",
                "/econews/comments/like",
                CUSTOM_SHOPPING_LIST_ITEMS,
                "/files/image",
                "/files/convert",
                HABIT_ASSIGN_ID,
                HABIT_ASSIGN_ID + "/custom",
                "/habit/assign/{habitAssignId}/enroll/**",
                "/habit/assign/{habitAssignId}/unenroll/{date}",
                "/habit/statistic/{habitId}",
                USER_CUSTOM_SHOPPING_LIST_ITEMS,
                USER_SHOPPING_LIST,
                "/user/{userId}/habit",
                "/habit/custom",
                "/custom/shopping-list-items/{userId}/{habitId}/custom-shopping-list-items",
                "/friends/{friendId:[0-9]+}")
            .hasAnyRole(USER, ADMIN, MODERATOR, UBS_EMPLOYEE)
            .antMatchers(HttpMethod.PUT,
                "/habit/statistic/{id}",
                "/econews/update",
                "/ownSecurity",
                "/user/profile",
                HABIT_ASSIGN_ID + "/update-habit-duration",
                "/habit/assign/{habitAssignId}/updateProgressNotificationHasDisplayed",
                HABIT_ASSIGN_ID + "/allUserAndCustomList")
            .hasAnyRole(USER, ADMIN, MODERATOR, UBS_EMPLOYEE)
            .antMatchers(HttpMethod.PATCH,
                ECONEWS_COMMENTS,
                CUSTOM_SHOPPING_LIST_ITEMS,
                CUSTOM_SHOPPING_LIST_URL,
                HABIT_ASSIGN_ID,
                "/shopping-list-items/shoppingList/{userId}",
                HABIT_ASSIGN_ID,
                "/habit/assign/cancel/{habitId}",
                USER_CUSTOM_SHOPPING_LIST_ITEMS,
                USER_SHOPPING_LIST + "/{shoppingListItemId}/status/{status}",
                USER_SHOPPING_LIST + "/{userShoppingListItemId}",
                "/user/profilePicture",
                "/user/deleteProfilePicture",
                "/friends/{friendId}/acceptFriend")
            .hasAnyRole(USER, ADMIN, MODERATOR, UBS_EMPLOYEE)
            .antMatchers(HttpMethod.DELETE,
                ECONEWS_COMMENTS,
                "/events/comments/{eventCommentId}",
                "/econews/{econewsId}",
                CUSTOM_SHOPPING_LIST_ITEMS,
                CUSTOM_SHOPPING_LIST_URL,
                "/favorite_place/{placeId}",
                "/social-networks",
                "/friends/{friendId}",
                USER_CUSTOM_SHOPPING_LIST_ITEMS,
                USER_SHOPPING_LIST + "/user-shopping-list-items",
                "/notifications/{notificationId:[0-9]+}",
                "/friends/{friendId}/declineFriend")
            .hasAnyRole(USER, ADMIN, MODERATOR, UBS_EMPLOYEE)
            .antMatchers(HttpMethod.GET,
                "/newsSubscriber",
                "/comments",
                "/comments/{id}",
                "/user/all",
                "/user/roles")
            .hasAnyRole(ADMIN, MODERATOR)
            .antMatchers(HttpMethod.POST,
                "/place/filter/predicate")
            .hasAnyRole(ADMIN, MODERATOR)
            .antMatchers(HttpMethod.PUT,
                "/place/update/")
            .hasAnyRole(ADMIN, MODERATOR)
            .antMatchers(HttpMethod.POST,
                "/facts",
                "/user/filter")
            .hasAnyRole(ADMIN)
            .antMatchers(HttpMethod.PUT,
                "/facts/{factId}")
            .hasAnyRole(ADMIN)
            .antMatchers(HttpMethod.PATCH,
                "/user",
                "/user/status",
                "/user/role",
                "/user/update/role")
            .hasAnyRole(ADMIN)
            .antMatchers(HttpMethod.DELETE,
                "/facts/{factId}",
                "/comments")
            .hasAnyRole(ADMIN)
            .anyRequest().hasAnyRole(ADMIN, USER)
            .and()
            .logout()
            .logoutUrl("/logout")
            .logoutRequestMatcher(new AntPathRequestMatcher("/management/logout", "GET"))
            .clearAuthentication(true)
            .invalidateHttpSession(true)
            .deleteCookies("accessToken")
            .logoutSuccessUrl("/");
    }

    /**
     * Method for configure matchers that will be ignored in security.
     *
     * @param web {@link WebSecurity}
     */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/v2/api-docs/**");
        web.ignoring().antMatchers("/swagger.json");
        web.ignoring().antMatchers("/swagger-ui.html");
        web.ignoring().antMatchers("/swagger-resources/**");
        web.ignoring().antMatchers("/webjars/**");
    }

    /**
     * Method for configure type of authentication provider.
     *
     * @param auth {@link AuthenticationManagerBuilder}
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(new JwtAuthenticationProvider(jwtTool));
    }

    /**
     * Provides AuthenticationManager.
     *
     * @return {@link AuthenticationManager}
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    /**
     * Bean {@link CorsConfigurationSource} that uses for CORS setup.
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(
            Arrays.asList("GET", "POST", "OPTIONS", "DELETE", "PUT", "PATCH"));
        configuration.setAllowedHeaders(
            Arrays.asList(
                "X-Requested-With", "Origin", "Content-Type", "Accept", "Authorization"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
