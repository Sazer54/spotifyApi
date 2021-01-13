package Music_Advisor;

import java.util.ArrayList;

public class Model {
    private final String CLIENT_ID = "88e8cea0a20447ddb283f92a94975e7d";
    private final String CLIENT_SECRET = "7e1d66f5269548b09b1a4a9cba964e86";
    private final String REDIRECT_URI = "http://localhost:8080";
    private int entriesPerPage;
    private int currentFirstEntryIndex;
    private boolean authorised;
    private String operationSelection;
    private String apiPath;
    private String authCode;
    private String authPath;
    private String accessTokenInJson;
    private String accessToken;
    private ArrayList<String> newAlbums;
    private ArrayList<String> newArtists;
    private ArrayList<String> newLinks;
    private ArrayList<String> featuredTitles;
    private ArrayList<String> featuredLinks;
    private ArrayList<String> categories;
    private ArrayList<String> chosenPlaylistsTitles;
    private ArrayList<String> chosenPlaylistsLinks;
    private ArrayList<ArrayList<String>> currentlyUsedLists;

    public Model(String authPath, String apiPath, int entriesPerPage) {
        this.authPath = authPath;
        this.apiPath = apiPath;
        this.authorised = false;
        this.entriesPerPage = entriesPerPage;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }

    public int getEntriesPerPage() {
        return entriesPerPage;
    }

    public void setEntriesPerPage(int entriesPerPage) {
        this.entriesPerPage = entriesPerPage;
    }

    public int getCurrentFirstEntryIndex() {
        return currentFirstEntryIndex;
    }

    public void setCurrentFirstEntryIndex(int currentFirstEntryIndex) {
        this.currentFirstEntryIndex = currentFirstEntryIndex;
    }

    public void setCurrentlyUsedLists(ArrayList<String>... lists) {
        if (this.currentlyUsedLists == null) {
            this.currentlyUsedLists = new ArrayList<>();
        }
        this.currentlyUsedLists.clear();
        for (ArrayList<String> list : lists) {
            this.currentlyUsedLists.add(list);
        }
    }

    public ArrayList<ArrayList<String>> getCurrentlyUsedLists() {
        return currentlyUsedLists;
    }

    public void addNewAlbum(String albumName) {
        if (this.newAlbums == null) {
            this.newAlbums = new ArrayList<>();
        }
        this.newAlbums.add(albumName);
    }

    public void addNewArtist(String artistName) {
        if (this.newArtists == null) {
            this.newArtists = new ArrayList<>();
        }
        this.newArtists.add(artistName);
    }

    public void addNewLink(String link) {
        if (this.newLinks == null) {
            this.newLinks = new ArrayList<>();
        }
        this.newLinks.add(link);
    }

    public ArrayList<String> getNewAlbums() {
        return newAlbums;
    }

    public ArrayList<String> getNewArtists() {
        return newArtists;
    }

    public ArrayList<String> getNewLinks() {
        return newLinks;
    }

    public void addFeaturedTitle(String title) {
        if (this.featuredTitles == null) {
            this.featuredTitles = new ArrayList<>();
        }
        featuredTitles.add(title);
    }

    public void addFeaturedLink(String link) {
        if (this.featuredLinks == null) {
            this.featuredLinks = new ArrayList<>();
        }
        featuredLinks.add(link);
    }

    public ArrayList<String> getFeaturedTitles() {
        return featuredTitles;
    }

    public ArrayList<String> getFeaturedLinks() {
        return featuredLinks;
    }

    public void addCategory(String category) {
        if (this.categories == null) {
            this.categories = new ArrayList<>();
        }
        this.categories.add(category);
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public ArrayList<String> getChosenPlaylistsTitles() {
        return chosenPlaylistsTitles;
    }

    public ArrayList<String> getChosenPlaylistsLinks() {
        return chosenPlaylistsLinks;
    }

    public void addChosenPlaylistTitle(String title) {
        if (this.chosenPlaylistsTitles == null) {
            this.chosenPlaylistsTitles = new ArrayList<>();
        }
        this.chosenPlaylistsTitles.add(title);
    }

    public void addChosePlaylistLink(String link) {
        if (this.chosenPlaylistsLinks == null) {
            this.chosenPlaylistsLinks = new ArrayList<>();
        }
        this.chosenPlaylistsLinks.add(link);
    }

    public String getAccessTokenInJson() {
        return accessTokenInJson;
    }

    public void setAccessTokenInJson(String accessTokenInJson) {
        this.accessTokenInJson = accessTokenInJson;
    }

    public String getCLIENT_ID() {
        return CLIENT_ID;
    }

    public String getCLIENT_SECRET() {
        return CLIENT_SECRET;
    }

    public String getREDIRECT_URI() {
        return REDIRECT_URI;
    }
    public String getAuthPath() {
        return authPath;
    }

    public void setAuthPath(String authPath) {
        this.authPath = authPath;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public boolean isAuthorised() {
        return authorised;
    }

    public void setAuthorised(boolean hasAuthorised) {
        this.authorised = hasAuthorised;
    }

    public void setOperationSelection(String operationSelection) {
        this.operationSelection = operationSelection;
    }

    public String getOperationSelection() {
        return operationSelection;
    }
}
