package com.github.victormpcmun.bookmark2html.favicon;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Downloads favicons from Google's favicon service, all domains in parallel.
 * A domain that fails (no connection, timeout, unknown site) just gets no favicon.
 */
public class GoogleFaviconSource implements FaviconSource {

    private static final String SERVICE_URL = "https://www.google.com/s2/favicons?sz=32&domain=";
    private static final Duration TIMEOUT = Duration.ofSeconds(10);
    private static final String DEFAULT_TYPE = "image/png";
    private static final int HTTP_OK = 200;

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    @Override
    public Map<String, String> faviconsFor(Set<String> domains) {
        Map<String, CompletableFuture<Optional<String>>> downloads = domains.stream()
                .collect(Collectors.toMap(domain -> domain, this::download));
        return downloads.entrySet().stream()
                .filter(download -> download.getValue().join().isPresent())
                .collect(Collectors.toMap(Map.Entry::getKey, download -> download.getValue().join().get()));
    }

    private CompletableFuture<Optional<String>> download(String domain) {
        return client.sendAsync(requestFor(domain), HttpResponse.BodyHandlers.ofByteArray())
                .thenApply(this::toDataUri)
                .exceptionally(error -> Optional.empty());
    }

    private HttpRequest requestFor(String domain) {
        return HttpRequest.newBuilder(URI.create(SERVICE_URL + domain))
                .timeout(TIMEOUT)
                .build();
    }

    private Optional<String> toDataUri(HttpResponse<byte[]> response) {
        if (response.statusCode() != HTTP_OK) {
            return Optional.empty();
        }
        return Optional.of("data:" + contentTypeOf(response) + ";base64,"
                + Base64.getEncoder().encodeToString(response.body()));
    }

    private String contentTypeOf(HttpResponse<byte[]> response) {
        return response.headers().firstValue("Content-Type").orElse(DEFAULT_TYPE);
    }
}
