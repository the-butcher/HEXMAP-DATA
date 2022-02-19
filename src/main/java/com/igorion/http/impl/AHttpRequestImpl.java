package com.igorion.http.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.bind.DatatypeConverter;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import com.igorion.app.impl.C19Application;
import com.igorion.fail.EFailureCode;
import com.igorion.fail.impl.C19Failure;
import com.igorion.http.IHttpRequest;
import com.igorion.http.IHttpResponse;
import com.igorion.http.IHttpResult;
import com.igorion.http.IOutboundHttpConfigEdit;
import com.igorion.http.IResponseHandler;
import com.igorion.logs.ELogger;
import com.igorion.type.live.ICredentials;

public abstract class AHttpRequestImpl<T, R extends HttpRequestBase> implements IHttpRequest<T> {

    private final String urlRaw;
    private final IResponseHandler<T> responseHandler;
    private final Map<String, String> parameters;
    private final IOutboundHttpConfigEdit outboundHttpConfigEdit;
    private final Map<String, String> headers;

    AHttpRequestImpl(String urlRaw, IResponseHandler<T> responseHandler) {

        this.urlRaw = urlRaw;
        this.responseHandler = responseHandler;
        this.parameters = new LinkedHashMap<>();
        this.headers = new LinkedHashMap<>();

        this.outboundHttpConfigEdit = OutboundHttpConfig.composite();
        this.outboundHttpConfigEdit.addSubConfig(C19Application.getInstance().getOutboundHttpConfig());

    }

    /**
     * build the {@link CloseableHttpClient} used for this request by creating a new instance of {@link HttpClientBuilder}<br>
     * then applying this instance's outboundHttpConfig<br>
     *
     * @return
     */
    protected CloseableHttpClient buildHttpClient() {
        return this.outboundHttpConfigEdit.getOutboundHttpConfig().applyTo(HttpClientBuilder.create()).build();
    }

    @Override
    public String getUrlRaw() {
        return this.urlRaw;
    }

    @Override
    public void setHeader(String name, String value) {
        this.headers.put(name, value);
    }

    protected static String encodeBase64(String value) {
        return DatatypeConverter.printBase64Binary(value.getBytes(StandardCharsets.ISO_8859_1));
    }

    @Override
    public void setAuthorizationHeader(ICredentials credentials) {
        String authorization = encodeBase64(String.format("%s:%s", credentials.getUsername(), credentials.getPassword().getValue()));
        setHeader(HttpHeaders.AUTHORIZATION, String.format("Basic %s", authorization));
    }

    @Override
    public void setParameter(String name, String value) {
        this.parameters.put(name, value);
    }

    @Override
    public Optional<String> optParameter(String name) {
        return Optional.ofNullable(this.parameters.get(name));
    }

    protected static Header toHttpHeader(Entry<String, String> parameterEntry) {
        return new BasicHeader(parameterEntry.getKey(), parameterEntry.getValue());
    }

    protected static NameValuePair toHttpParameter(Entry<String, String> parameterEntry) {
        return new BasicNameValuePair(parameterEntry.getKey(), parameterEntry.getValue());
    }

    @Override
    public List<NameValuePair> getHttpParameterList() {
        return this.parameters.entrySet().stream().map(AHttpRequestImpl::toHttpParameter).collect(Collectors.toList());
    }

    protected List<Header> getHttpHeaderList() {
        return this.headers.entrySet().stream().map(AHttpRequestImpl::toHttpHeader).collect(Collectors.toList());
    }

    protected abstract R createRequest() throws IOException, URISyntaxException;

    @Override
    public IHttpResponse<T> send() throws C19Failure {

        try (CloseableHttpClient httpClient = buildHttpClient()) {

            ELogger.HTTP.debug(() -> String.format("issuing request (%s: %s)", getRequestMethod(), this.urlRaw));

            HttpRequestBase getRequest = createRequest();
            IHttpResult<T> responseResult = httpClient.execute(getRequest, this.responseHandler);

            ELogger.HTTP.debug(() -> String.format("done issuing request (%s: %s)", getRequestMethod(), this.urlRaw));

            return new HttpResponseImpl<>(responseResult.getStatusCode(), responseResult.getOutputEntity()); //error that may have occurred when handling the request would be thrown here

        } catch (IOException | URISyntaxException ioFailure) {
            String message = String.format("failed to issue request (%s: %s)", getRequestMethod(), this.urlRaw);
            throw new C19Failure(EFailureCode.INVALID_GATEWAY, message, ioFailure);
        }

    }

}
