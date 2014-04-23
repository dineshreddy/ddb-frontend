package de.ddb.next.filter

import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse

/**
 * Sample filter implementation to define a set of Content Security Policies.<br/>
 *
 * This implementation has a dependency on Commons Codec API.<br/>
 *
 * This filter set CSP policies using all HTTP headers defined into W3C specification.<br/>
 * <br/>
 * This implementation is oriented to be easily understandable and easily adapted.<br/>
 *
 */
public class CSPPoliciesApplier {

    private static final String KEYWORD_NONE = "'none'"
    private static final String KEYWORD_SELF = "'self'"
    private static final String KEYWORD_UNSAFE_EVAL = "'unsafe-eval'"
    private static final String KEYWORD_UNSAFE_INLINE = "'unsafe-inline'"

    /** List CSP HTTP Headers */
    private final List<String> cspHeaders = new ArrayList<String>()

    /** Collection of CSP polcies that will be applied */
    private final String policies

    public CSPPoliciesApplier() {
        // Define list of CSP HTTP Headers
        cspHeaders.add("Content-Security-Policy")
        cspHeaders.add("X-Content-Security-Policy")
        cspHeaders.add("X-WebKit-CSP")

        // Define CSP policies
        // Loading policies for Frame and Sandboxing will be dynamically defined : We need to know if context use Frame
        List<String> cspPolicies = new ArrayList<String>()
        // --Disable default source in order to avoid browser fallback loading using 'default-src' locations
        cspPolicies.add("default-src " + KEYWORD_NONE)
        // --Define loading policies for Scripts
        cspPolicies.add("script-src " + KEYWORD_SELF + " " + KEYWORD_UNSAFE_INLINE + " " + KEYWORD_UNSAFE_EVAL)
        // --Define loading policies for Plugins
        cspPolicies.add("object-src " + KEYWORD_SELF)
        // --Define loading policies for Styles (CSS)
        cspPolicies.add("style-src " + KEYWORD_SELF + " " + KEYWORD_UNSAFE_INLINE)
        // --Define loading policies for Images
        cspPolicies.add("img-src *")
        // --Define loading policies for Audios/Videos
        cspPolicies.add("media-src " + KEYWORD_SELF)
        // --Define loading policies for Frames
        cspPolicies.add("font-src " + KEYWORD_SELF)
        // --Define loading policies for Connection
        cspPolicies.add("connect-src " + KEYWORD_SELF)

        // Target formatting
        policies = cspPolicies.toString().replaceAll("(\\[|\\])", "").replaceAll(",", ";").trim()
    }

    /**
     * Add CSP policies on each HTTP response.
     *
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void applyPolicies(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        /* Step 1 : Detect if target resource is a Frame */
        // TODO: Customize here according to your context...
        boolean isFrame = false

        /* Step 2 : Add CSP policies to HTTP response */
        StringBuilder policiesBuffer = new StringBuilder(policies)

        // If resource is a frame add Frame/Sandbox CSP policy
        if (isFrame) {
            // Frame + Sandbox : Here sandbox allow nothing, customize sandbox options depending on your app....
            policiesBuffer.append(";").append("frame-src " + KEYWORD_SELF + ";sandbox")
        }

        // Add policies to all HTTP headers
        for (String header : cspHeaders) {
            response.setHeader(header, policiesBuffer.toString())
        }
    }
}