package Music_Advisor;

import com.google.gson.JsonObject;

import java.util.ArrayList;

public class View {
    public void printAccessTokenResponse(String response) {
        System.out.println("response: " + response);
    }

    public void printErrorMessage(JsonObject errorInJson) {
        JsonObject errorObject = errorInJson.get("error").getAsJsonObject();
        String errorMessage = errorObject.get("message").getAsString();
        System.out.println(errorMessage);
    }

    public void showAuthLink(String accessPath, String clientId, String redirectUri) {
        System.out.println("use this link to request the access code:");
        System.out.println(accessPath +
                "/authorize?"
                + "client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code");
        System.out.println("waiting for code...");
    }

    public void printReleases(ArrayList<ArrayList<String>> lists, int entriesPerPage, int startingIndex, boolean breakLine) {
        if (lists.size() == 0) {
            System.out.println("No lists found");
        } else {
            int listLength = lists.get(0).size();
            int numberOfLists = lists.size();
            if ((startingIndex < listLength) && (startingIndex >= 0)) {
                int pagesTotal = (int) Math.round((double) listLength / (double) entriesPerPage);
                int currentPageNumber = (startingIndex / entriesPerPage) + 1;
                for (int i = startingIndex; i < Math.min(listLength, startingIndex + entriesPerPage); i++) {
                    for (int j = 0; j < numberOfLists; j++) {
                        try {
                            System.out.println(lists.get(j).get(i));
                        } catch (IndexOutOfBoundsException e) {
                            break;
                        }
                    }
                    if (breakLine) System.out.println();
                }
                System.out.println("---PAGE " + currentPageNumber + " OF " + pagesTotal + "---");
            } else {
                System.out.println("No more pages.");
            }
        }
    }

    public void printNoMorePages() {
        System.out.println("No more pages");
    }
}
