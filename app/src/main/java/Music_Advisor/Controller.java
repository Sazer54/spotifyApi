
package Music_Advisor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Controller {

    private Model model;
    private View view;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
    }

    public void work(String authPath, String apiPath) throws IOException, InterruptedException {
        while (!"exit".equals(model.getOperationSelection())) {
            this.promptSelection();
            this.checkAuthorisation();
            this.updateView();
        }
    }

    public void promptSelection() {
        Scanner scanner = new Scanner(System.in);
        model.setOperationSelection(scanner.nextLine());
    }

    private void checkAuthorisation() throws IOException, InterruptedException {
        if (model.getOperationSelection().equals("auth") && !model.isAuthorised()) {
            this.authorise();
        }
    }

    private void updateView() {
        if (model.isAuthorised()) {
            switch (model.getOperationSelection()) {
                case "new":
                    this.getNewReleases();
                    model.setCurrentlyUsedLists(model.getNewAlbums(), model.getNewArtists(), model.getNewLinks());
                    model.setCurrentFirstEntryIndex(0);
                    view.printReleases(model.getCurrentlyUsedLists(), model.getEntriesPerPage(), model.getCurrentFirstEntryIndex(), true);
                    break;
                case "featured":
                    this.getFeaturedReleases();
                    model.setCurrentlyUsedLists(model.getFeaturedTitles(), model.getFeaturedLinks());
                    model.setCurrentFirstEntryIndex(0);
                    view.printReleases(model.getCurrentlyUsedLists(), model.getEntriesPerPage(), model.getCurrentFirstEntryIndex(), true);
                    break;
                case "categories":
                    this.getCategories();
                    model.setCurrentlyUsedLists(model.getCategories());
                    model.setCurrentFirstEntryIndex(0);
                    view.printReleases(model.getCurrentlyUsedLists(), model.getEntriesPerPage(), model.getCurrentFirstEntryIndex(), false);
                    //view.printCategories(model.getCurrentlyUsedLists().get(0), model.getEntriesPerPage(), model.getCurrentFirstEntryIndex()); // inconsequential
                    break;
                case "exit":
                    System.out.println("---GOODBYE!---");
                    System.exit(0);
                    break;
                case "auth":
                    System.out.println("---SUCCESS---");
                    break;
                case "next":
                    model.setCurrentFirstEntryIndex(model.getCurrentFirstEntryIndex() + model.getEntriesPerPage());
                    if (model.getCurrentFirstEntryIndex() >= model.getCurrentlyUsedLists().get(0).size()) {
                        model.setCurrentFirstEntryIndex(model.getCurrentFirstEntryIndex() - model.getEntriesPerPage());
                        view.printNoMorePages();
                    } else {
                        view.printReleases(model.getCurrentlyUsedLists(), model.getEntriesPerPage(), model.getCurrentFirstEntryIndex(),
                                            model.getCategories() != model.getCurrentlyUsedLists().get(0));
                    }
                    break;
                case "previous":
                    model.setCurrentFirstEntryIndex(model.getCurrentFirstEntryIndex() - model.getEntriesPerPage());
                    if (model.getCurrentFirstEntryIndex() < 0) {
                        model.setCurrentFirstEntryIndex(model.getCurrentFirstEntryIndex() + model.getEntriesPerPage());
                        view.printNoMorePages();
                    } else {
                        System.out.println(model.getCurrentFirstEntryIndex());
                        view.printReleases(model.getCurrentlyUsedLists(), model.getEntriesPerPage(), model.getCurrentFirstEntryIndex(),
                                model.getCategories() != model.getCurrentlyUsedLists().get(0));
                    }
                    break;
                default:
                    if (model.getOperationSelection().contains("playlists")) {
                        String playlistType = model.getOperationSelection().substring(model.getOperationSelection().indexOf(" ") + 1);
                        this.getPlaylists(playlistType);
                        model.setCurrentlyUsedLists(model.getChosenPlaylistsTitles(), model.getChosenPlaylistsLinks());
                        model.setCurrentFirstEntryIndex(0);
                        view.printReleases(model.getCurrentlyUsedLists(), model.getEntriesPerPage(), model.getCurrentFirstEntryIndex(), true);
                    } else {
                        System.out.println("Wrong selection");
                    }
                    break;
            }
        } else {
            System.out.println("Please, provide access for application.");
        }
    }

    private void authorise() throws IOException, InterruptedException {
        this.getAuthCode();
        if (!model.getAuthCode().equals("access_denied")) {
            this.getAccessTokenInJson();
            this.retrieveAccessToken();
            view.printAccessTokenResponse(model.getAccessTokenInJson());
            model.setAuthorised(true);
        }
    }

    private void getAuthCode() {
        try {
            model.setAuthCode(null);
            HttpServer httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(8080), 0);
            httpServer.start();
            httpServer.createContext("/", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    String query = exchange.getRequestURI().getQuery();
                    String response;
                    if (query != null && query.contains("code")) {
                        response = "Got the code. Return back to your program.";
                        System.out.println("code received");
                    } else {
                        response = "Authorization code not found. Try again.";
                        System.out.println("code not received");
                    }
                    model.setAuthCode(query.split("=")[1]);
                    exchange.sendResponseHeaders(200, response.length());
                    exchange.getResponseBody().write(response.getBytes());
                    exchange.getResponseBody().close();
                }
            });
            view.showAuthLink(model.getAuthPath(), model.getCLIENT_ID(), model.getREDIRECT_URI());
            while (model.getAuthCode() == null) {
                Thread.sleep(10);
            }
            httpServer.stop(1);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void getAccessTokenInJson() {
        System.out.println("making http request for access_token...");
        String requestBody = "client_id=" + model.getCLIENT_ID()
                + "&client_secret=" + model.getCLIENT_SECRET()
                + "&grant_type=authorization_code"
                + "&code=" + model.getAuthCode()
                + "&redirect_uri=" + model.getREDIRECT_URI();
        HttpRequest accessTokenInJSONRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create(model.getAuthPath() + "/api/token"))
                .build();
        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse<String> response = null;
        try {
            response = client.send(accessTokenInJSONRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        model.setAccessTokenInJson(response.body());
    }

    private void retrieveAccessToken() {
        JsonObject accessTokenJsonObject = JsonParser.parseString(model.getAccessTokenInJson()).getAsJsonObject();
        model.setAccessToken(accessTokenJsonObject.get("access_token").getAsString());
    }

    private void getNewReleases() {
        String apiHref = model.getApiPath() + "/v1/browse/new-releases";
        String newReleasesInJson;
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest newPlaylistsRequest = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + model.getAccessToken())
                .uri(URI.create(apiHref))
                .GET()
                .build();
        HttpResponse<String> response = null;

        try {
            response = client.send(newPlaylistsRequest, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        newReleasesInJson = response.body();
        JsonObject newReleasesJsonObject = JsonParser.parseString(newReleasesInJson).getAsJsonObject();
        if (newReleasesInJson.contains("error")) {
            view.printErrorMessage(newReleasesJsonObject);
        } else {
            JsonObject albumsJsonObject = newReleasesJsonObject.getAsJsonObject("albums");
            StringBuilder tempArtists;
            JsonObject urlObject;

            for (JsonElement albumObject : albumsJsonObject.getAsJsonArray("items")) {
                tempArtists = new StringBuilder("[");
                model.addNewAlbum((((JsonObject) albumObject).get("name").getAsString()));
                for (JsonElement artist : ((JsonObject) albumObject).getAsJsonArray("artists")) {
                    tempArtists.append(((JsonObject) artist).get("name").getAsString());
                    tempArtists.append(", ");
                }
                tempArtists.replace(tempArtists.lastIndexOf(","), tempArtists.lastIndexOf(",") + 2, "]");
                model.addNewArtist(tempArtists.toString());
                urlObject = ((JsonObject) albumObject).getAsJsonObject("external_urls");
                model.addNewLink(urlObject.get("spotify").getAsString());
            }
            model.setCurrentlyUsedLists(model.getNewAlbums(), model.getNewArtists(), model.getNewLinks());
        }
    }

    private void getFeaturedReleases() {
        String apiHref = model.getApiPath() + "/v1/browse/featured-playlists";
        String featuredReleasesInJson;
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest newPlaylistsRequest = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + model.getAccessToken())
                .uri(URI.create(apiHref))
                .GET()
                .build();
        HttpResponse<String> response = null;

        try {
            response = client.send(newPlaylistsRequest, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        featuredReleasesInJson = response != null ? response.body() : null;
        JsonObject featuredJsonObject = JsonParser.parseString(featuredReleasesInJson).getAsJsonObject();
        if (featuredReleasesInJson.contains("error")) {
            view.printErrorMessage(featuredJsonObject);
        } else {
            JsonObject playlistsJsonObject = featuredJsonObject.getAsJsonObject("playlists");
            JsonObject urlObject;
            for (JsonElement playlist : playlistsJsonObject.getAsJsonArray("items")) {
                model.addFeaturedTitle(((JsonObject) playlist).get("name").getAsString());
                urlObject = ((JsonObject)playlist).getAsJsonObject("external_urls");
                model.addFeaturedLink(urlObject.get("spotify").getAsString());
            }
        }
    }

    private void getCategories() {
        String apiHref = model.getApiPath() + "/v1/browse/categories";
        String categoriesInJson;
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest newPlaylistsRequest = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + model.getAccessToken())
                .uri(URI.create(apiHref))
                .GET()
                .build();
        HttpResponse<String> response = null;

        try {
            response = client.send(newPlaylistsRequest, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        categoriesInJson = response.body();
        JsonObject categoriesAsJsonObject = JsonParser.parseString(categoriesInJson).getAsJsonObject();
        if (categoriesInJson.contains("error")) {
            view.printErrorMessage(categoriesAsJsonObject);
        }
        else {
            JsonObject categoriesExtracted = categoriesAsJsonObject.getAsJsonObject("categories");
            for (JsonElement category : categoriesExtracted.getAsJsonArray("items")) {
                model.addCategory(((JsonObject)category).get("name").getAsString());
            }
        }
    }

    private void getPlaylists(String type) {
        String categoriesApiHref = model.getApiPath() + "/v1/browse/categories";
        String playlistsInJson;
        String categoriesInJson;
        Map<String, String> categoriesIdMap = new HashMap<>();
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> hrefs = new ArrayList<>();
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest categoriesRequest = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + model.getAccessToken())
                .uri(URI.create(categoriesApiHref))
                .GET()
                .build();
        HttpResponse<String> responseCategories = null;

        try {
            responseCategories = client.send(categoriesRequest, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        categoriesInJson = responseCategories != null ? responseCategories.body() : null;
        JsonObject categoriesAsJsonObject = JsonParser.parseString(categoriesInJson).getAsJsonObject();
        JsonObject categoriesExtracted = categoriesAsJsonObject.getAsJsonObject("categories");
        for (JsonElement category : categoriesExtracted.getAsJsonArray("items")) {
            categoriesIdMap.put(((JsonObject) category).get("name").getAsString(), ((JsonObject) category).get("id").getAsString());
        }
        String playlist_href = model.getApiPath() + "/v1/browse/categories/" + categoriesIdMap.get(type) + "/playlists";
        HttpRequest playlistsRequest = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + model.getAccessToken())
                .uri(URI.create(playlist_href))
                .GET()
                .build();
        HttpResponse<String> responsePlaylists = null;
        try {
            responsePlaylists = client.send(playlistsRequest, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        playlistsInJson = responsePlaylists != null ? responsePlaylists.body() : null;
        JsonObject playlistsAsJsonObject = JsonParser.parseString(playlistsInJson).getAsJsonObject();
        if (playlistsInJson.contains("error")) {
            view.printErrorMessage(playlistsAsJsonObject);
        }
        else {
            try {
                JsonObject actualPlaylistsAsJsonObject = playlistsAsJsonObject.get("playlists").getAsJsonObject();
                JsonObject urlObject;
                for (JsonElement playlist : actualPlaylistsAsJsonObject.getAsJsonArray("items")) {
                    model.addChosenPlaylistTitle(((JsonObject)playlist).get("name").getAsString());
                    urlObject = ((JsonObject) playlist).get("external_urls").getAsJsonObject();
                    model.addChosePlaylistLink(urlObject.get("spotify").getAsString());
                }
            } catch (NullPointerException e) {
                System.out.println("Unknown category name.");
            }
        }

    }
}


