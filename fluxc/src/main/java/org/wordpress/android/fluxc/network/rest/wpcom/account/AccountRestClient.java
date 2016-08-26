package org.wordpress.android.fluxc.network.rest.wpcom.account;

import android.support.annotation.NonNull;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;
import org.wordpress.android.fluxc.Dispatcher;
import org.wordpress.android.fluxc.Payload;
import org.wordpress.android.fluxc.action.AccountAction;
import org.wordpress.android.fluxc.generated.AccountActionBuilder;
import org.wordpress.android.fluxc.generated.endpoint.WPCOMREST;
import org.wordpress.android.fluxc.model.AccountModel;
import org.wordpress.android.fluxc.network.BaseRequest.BaseErrorListener;
import org.wordpress.android.fluxc.network.BaseRequest.BaseNetworkError;
import org.wordpress.android.fluxc.network.UserAgent;
import org.wordpress.android.fluxc.network.rest.wpcom.BaseWPComRestClient;
import org.wordpress.android.fluxc.network.rest.wpcom.WPComGsonRequest;
import org.wordpress.android.fluxc.network.rest.wpcom.auth.AccessToken;
import org.wordpress.android.fluxc.network.rest.wpcom.auth.AppSecrets;
import org.wordpress.android.fluxc.store.AccountStore.NewUserError;
import org.wordpress.android.fluxc.store.AccountStore.NewUserErrorType;
import org.wordpress.android.util.AppLog;
import org.wordpress.android.util.AppLog.T;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AccountRestClient extends BaseWPComRestClient {
    private final AppSecrets mAppSecrets;

    public static class AccountRestPayload extends Payload {
        public AccountRestPayload(AccountModel account, BaseNetworkError error) {
            this.account = account;
            this.error = error;
        }
        public boolean isError() { return error != null; }
        public BaseNetworkError error;
        public AccountModel account;
    }

    public static class AccountPushSettingsResponsePayload extends Payload {
        public AccountPushSettingsResponsePayload(BaseNetworkError error) {
            this.error = error;
        }
        public boolean isError() {
            return error != null;
        }
        public BaseNetworkError error;
        public Map<String, Object> settings;
    }

    public static class NewAccountResponsePayload extends Payload {
        public NewUserError error;
        public boolean dryRun;
    }

    @Inject
    public AccountRestClient(Dispatcher dispatcher, RequestQueue requestQueue, AppSecrets appSecrets,
                             AccessToken accessToken, UserAgent userAgent) {
        super(dispatcher, requestQueue, accessToken, userAgent);
        mAppSecrets = appSecrets;
    }

    /**
     * Performs an HTTP GET call to the v1.1 /me/ endpoint. Upon receiving a
     * response (success or error) a {@link AccountAction#FETCHED_ACCOUNT} action is dispatched
     * with a payload of type {@link AccountRestPayload}. {@link AccountRestPayload#isError()} can
     * be used to determine the result of the request.
     */
    public void fetchAccount() {
        String url = WPCOMREST.me.getUrlV1_1();
        add(new WPComGsonRequest<>(Method.GET, url, null, AccountResponse.class,
                new Listener<AccountResponse>() {
                    @Override
                    public void onResponse(AccountResponse response) {
                        AccountModel account = responseToAccountModel(response);
                        AccountRestPayload payload = new AccountRestPayload(account, null);
                        mDispatcher.dispatch(AccountActionBuilder.newFetchedAccountAction(payload));
                    }
                },
                new BaseErrorListener() {
                    @Override
                    public void onErrorResponse(@NonNull BaseNetworkError error) {
                        AccountRestPayload payload = new AccountRestPayload(null, error);
                        mDispatcher.dispatch(AccountActionBuilder.newFetchedAccountAction(payload));
                    }
                }
        ));
    }

    /**
     * Performs an HTTP GET call to the v1.1 /me/settings/ endpoint. Upon receiving
     * a response (success or error) a {@link AccountAction#FETCHED_SETTINGS} action is dispatched
     * with a payload of type {@link AccountRestPayload}. {@link AccountRestPayload#isError()} can
     * be used to determine the result of the request.
     */
    public void fetchAccountSettings() {
        String url = WPCOMREST.me.settings.getUrlV1_1();
        add(new WPComGsonRequest<>(Method.GET, url, null, AccountSettingsResponse.class,
                new Listener<AccountSettingsResponse>() {
                    @Override
                    public void onResponse(AccountSettingsResponse response) {
                        AccountModel settings = responseToAccountSettingsModel(response);
                        AccountRestPayload payload = new AccountRestPayload(settings, null);
                        mDispatcher.dispatch(AccountActionBuilder.newFetchedSettingsAction(payload));
                    }
                },
                new BaseErrorListener() {
                    @Override
                    public void onErrorResponse(@NonNull BaseNetworkError error) {
                        AccountRestPayload payload = new AccountRestPayload(null, error);
                        mDispatcher.dispatch(AccountActionBuilder.newFetchedSettingsAction(payload));
                    }
                }
        ));
    }

    /**
     * Performs an HTTP POST call to the v1.1 /me/settings/ endpoint. Upon receiving
     * a response (success or error) a {@link AccountAction#PUSHED_SETTINGS} action is dispatched
     * with a payload of type {@link AccountPushSettingsResponsePayload}.
     * {@link AccountPushSettingsResponsePayload#isError()} can be used to determine the result of the request.
     *
     * No HTTP POST call is made if the given parameter map is null or contains no entries.
     */
    public void postAccountSettings(Map<String, String> params) {
        if (params == null || params.isEmpty()) return;
        String url = WPCOMREST.me.settings.getUrlV1_1();
        // Note: we have to use a HashMap as a response here because the API response format is different depending
        // of the request we do.
        add(new WPComGsonRequest<>(Method.POST, url, params, HashMap.class,
                new Listener<HashMap>() {
                    @Override
                    public void onResponse(HashMap response) {
                        AccountPushSettingsResponsePayload payload = new AccountPushSettingsResponsePayload(null);
                        payload.settings = response;
                        mDispatcher.dispatch(AccountActionBuilder.newPushedSettingsAction(payload));
                    }
                },
                new BaseErrorListener() {
                    @Override
                    public void onErrorResponse(@NonNull BaseNetworkError error) {
                        AccountPushSettingsResponsePayload payload = new AccountPushSettingsResponsePayload(error);
                        mDispatcher.dispatch(AccountActionBuilder.newPushedSettingsAction(payload));
                    }
                }
        ));
    }

    public void newAccount(@NonNull String username, @NonNull String password, @NonNull String email,
                           final boolean dryRun) {
        String url = WPCOMREST.users.new_.getUrlV1();
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("email", email);
        params.put("validate", dryRun ? "1" : "0");
        params.put("client_id", mAppSecrets.getAppId());
        params.put("client_secret", mAppSecrets.getAppSecret());

        WPComGsonRequest<NewAccountResponse> request = new WPComGsonRequest<>(Method.POST, url, params,
                NewAccountResponse.class,
                new Listener<NewAccountResponse>() {
                    @Override
                    public void onResponse(NewAccountResponse response) {
                        NewAccountResponsePayload payload = new NewAccountResponsePayload();
                        payload.dryRun = dryRun;
                        mDispatcher.dispatch(AccountActionBuilder.newCreatedNewAccountAction(payload));
                    }
                },
                new BaseErrorListener() {
                    @Override
                    public void onErrorResponse(@NonNull BaseNetworkError error) {
                        NewAccountResponsePayload payload = volleyErrorToAccountResponsePayload(error.volleyError);
                        payload.dryRun = dryRun;
                        mDispatcher.dispatch(AccountActionBuilder.newCreatedNewAccountAction(payload));
                    }
                }
        );

        request.disableRetries();
        add(request);
    }

    private NewAccountResponsePayload volleyErrorToAccountResponsePayload(VolleyError error) {
        NewAccountResponsePayload payload = new NewAccountResponsePayload();
        payload.error = new NewUserError(NewUserErrorType.GENERIC_ERROR, "");
        if (error.networkResponse != null && error.networkResponse.data != null) {
            AppLog.e(T.API, new String(error.networkResponse.data));
            String jsonString = new String(error.networkResponse.data);
            try {
                JSONObject errorObj = new JSONObject(jsonString);
                payload.error.type = NewUserErrorType.fromString((String) errorObj.get("error"));
                payload.error.message = (String) errorObj.get("message");
            } catch (JSONException e) {
                // Do nothing (keep default error)
            }
        }
        return payload;
    }

    private AccountModel responseToAccountModel(AccountResponse from) {
        AccountModel account = new AccountModel();
        account.setUserId(from.ID);
        account.setDisplayName(from.display_name);
        account.setUserName(from.username);
        account.setEmail(from.email);
        account.setPrimarySiteId(from.primary_blog);
        account.setAvatarUrl(from.avatar_URL);
        account.setProfileUrl(from.profile_URL);
        account.setDate(from.date);
        account.setSiteCount(from.site_count);
        account.setVisibleSiteCount(from.visible_site_count);
        return account;
    }

    private AccountModel responseToAccountSettingsModel(AccountSettingsResponse from) {
        AccountModel account = new AccountModel();
        account.setUserName(from.user_login);
        account.setDisplayName(from.display_name);
        account.setFirstName(from.first_name);
        account.setLastName(from.last_name);
        account.setAboutMe(from.description);
        account.setNewEmail(from.new_user_email);
        account.setAvatarUrl(from.avatar_URL);
        account.setPendingEmailChange(from.user_email_change_pending);
        account.setWebAddress(from.user_URL);
        account.setPrimarySiteId(from.primary_site_ID);
        return account;
    }

    public static boolean updateAccountModelFromPushSettingsResponse(AccountModel accountModel,
                Map<String, Object> from) {
        AccountModel old = new AccountModel();
        old.copyAccountAttributes(accountModel);
        old.setId(accountModel.getId());
        old.copyAccountSettingsAttributes(accountModel);
        if (from.containsKey("display_name")) accountModel.setDisplayName((String) from.get("display_name"));
        if (from.containsKey("first_name")) accountModel.setFirstName((String) from.get("first_name"));
        if (from.containsKey("last_name")) accountModel.setLastName((String) from.get("last_name"));
        if (from.containsKey("description")) accountModel.setAboutMe((String) from.get("description"));
        if (from.containsKey("user_email")) accountModel.setEmail((String) from.get("user_email"));
        if (from.containsKey("user_email_change_pending")) accountModel.setPendingEmailChange((Boolean) from.get
                ("user_email_change_pending"));
        if (from.containsKey("new_user_email")) accountModel.setEmail((String) from.get("new_user_email"));
        if (from.containsKey("user_URL")) accountModel.setWebAddress((String) from.get("user_URL"));
        if (from.containsKey("primary_site_ID")) accountModel.setPrimarySiteId(
                ((Double) from.get("primary_site_ID")).longValue());
        return !old.equals(accountModel);
    }
}