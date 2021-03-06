package org.wordpress.android.fluxc.mocked

import android.content.Context
import com.android.volley.RequestQueue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.wordpress.android.fluxc.Dispatcher
import org.wordpress.android.fluxc.TestUtils
import org.wordpress.android.fluxc.module.MockedNetworkModule
import org.wordpress.android.fluxc.network.BaseRequest.BaseErrorListener
import org.wordpress.android.fluxc.network.Response
import org.wordpress.android.fluxc.network.UserAgent
import org.wordpress.android.fluxc.network.discovery.RootWPAPIRestResponse
import org.wordpress.android.fluxc.network.rest.wpcom.BaseWPComRestClient
import org.wordpress.android.fluxc.network.rest.wpcom.WPComGsonRequest
import org.wordpress.android.fluxc.network.rest.wpcom.WPComGsonRequest.WPComGsonNetworkError
import org.wordpress.android.fluxc.network.rest.wpcom.auth.AccessToken
import org.wordpress.android.fluxc.network.rest.wpcom.jetpacktunnel.JetpackTunnelGsonRequest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tests using a Mocked Network app component. Test the network client itself and not the underlying
 * network component(s).
 */
class MockedStack_JetpackTunnelTest : MockedStack_Base() {
    @Inject internal lateinit var jetpackTunnelClient: JetpackTunnelClientForTests

    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        mMockedNetworkAppComponent.inject(this)
    }

    @Test
    fun testErrorResponse() {
        val countDownLatch = CountDownLatch(1)
        val url = "/"

        val request = JetpackTunnelGsonRequest.buildGetRequest(url, MockedNetworkModule.FAILURE_SITE_ID, mapOf(),
                RootWPAPIRestResponse::class.java,
                { _: RootWPAPIRestResponse? ->
                    throw AssertionError("Unexpected success!")
                },
                BaseErrorListener {
                    error -> run {
                        // Verify that the error response is correctly parsed
                        assertEquals("rest_no_route", (error as WPComGsonNetworkError).apiError)
                        assertEquals("No route was found matching the URL and request method", error.message)
                        countDownLatch.countDown()
                    }
                })

        jetpackTunnelClient.exposedAdd(request)
        assertTrue(countDownLatch.await(TestUtils.DEFAULT_TIMEOUT_MS.toLong(), TimeUnit.MILLISECONDS))
    }

    @Test
    fun testSuccessfulGetRequest() {
        val countDownLatch = CountDownLatch(1)
        val url = "/"
        val params = mapOf("context" to "view")

        val request = JetpackTunnelGsonRequest.buildGetRequest(url, 567, params,
                RootWPAPIRestResponse::class.java,
                { response: RootWPAPIRestResponse? ->
                    run {
                        // Verify that the successful response is correctly parsed
                        assertTrue(response?.namespaces?.contains("wp/v2")!!)
                        countDownLatch.countDown()
                    }
                },
                BaseErrorListener {
                    error -> run {
                        throw AssertionError("Unexpected BaseNetworkError: "
                                + (error as WPComGsonNetworkError).apiError + " - " + error.message)
                    }
                })

        jetpackTunnelClient.exposedAdd(request)
        assertTrue(countDownLatch.await(TestUtils.DEFAULT_TIMEOUT_MS.toLong(), TimeUnit.MILLISECONDS))
    }

    @Test
    fun testSuccessfulPostRequest() {
        val countDownLatch = CountDownLatch(1)
        val url = "/wp/v2/settings/"

        val requestBody = mapOf<String, Any>("title" to "New Title", "description" to "New Description")

        val request = JetpackTunnelGsonRequest.buildPostRequest(url, 567, requestBody,
                SettingsAPIResponse::class.java,
                { response: SettingsAPIResponse? ->
                    run {
                        // Verify that the successful response is correctly parsed
                        assertEquals("New Title", response?.title)
                        assertEquals("New Description", response?.description)
                        assertNull(response?.language)
                        countDownLatch.countDown()
                    }
                },
                BaseErrorListener {
                    error -> run {
                        throw AssertionError("Unexpected BaseNetworkError: "
                                + (error as WPComGsonNetworkError).apiError + " - " + error.message)
                    }
                })

        jetpackTunnelClient.exposedAdd(request)
        assertTrue(countDownLatch.await(TestUtils.DEFAULT_TIMEOUT_MS.toLong(), TimeUnit.MILLISECONDS))
    }

    @Singleton
    class JetpackTunnelClientForTests @Inject constructor(appContext: Context, dispatcher: Dispatcher,
                                                          requestQueue: RequestQueue, accessToken: AccessToken,
                                                          userAgent: UserAgent
    ) : BaseWPComRestClient(appContext, dispatcher, requestQueue, accessToken, userAgent) {
        /**
         * Wraps and exposes the protected [add] method so that tests can add requests directly.
         */
        fun <T> exposedAdd(request: WPComGsonRequest<T>?) { add(request) }
    }

    class SettingsAPIResponse : Response {
        val title: String? = null
        val description: String? = null
        val language: String? = null
    }
}
