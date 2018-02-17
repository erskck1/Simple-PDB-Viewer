package visualization.utils;

import visualization.controller.BorderPaneController;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * The Class PDBDownloader.
 */
public class PDBDownloader {

    /**
     * The code.
     */
    private String code;

    /**
     * Instantiates a new PDB downloader.
     *
     * @param code the code
     */
    public PDBDownloader(String code) {
        this.code = code;
    }

    /**
     * Instantiates a new PDB downloader.
     */
    public PDBDownloader() {
        super();
    }

    /**
     * Downloads a PDB file based on input ID.
     *
     * @param code the code
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void download(String code) throws IOException {
        if (code.length() != 4) {
            System.out.println("Invalid PDB code. Must be 4 characters long.");
            BorderPaneController.showPopUp("Invalid PDB code. Must be 4 characters long.");
            return;
        }

        URL url = new URL("http://www.rcsb.org/pdb/files/" + code + ".pdb");
        String filename = code.toLowerCase() + ".pdb";
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        int response = conn.getResponseCode();

        if (response == HttpURLConnection.HTTP_OK) {
            InputStream input = conn.getInputStream();

            FileOutputStream outputFile = new FileOutputStream(filename);

            int bytesRead = -1;
            byte[] buffer = new byte[4096];
            while ((bytesRead = input.read(buffer)) != -1) {
                outputFile.write(buffer, 0, bytesRead);
            }

            outputFile.close();
            input.close();

            System.out.println("File successfully downloaded!");
        } else if (response == HttpURLConnection.HTTP_NOT_FOUND) {
            System.out.println("Specified ID does not exist in database.");
            BorderPaneController.showPopUp("ID not in database.");
        } else {
            System.out.println("Connection Error!");
            BorderPaneController.showPopUp("Connection Error!");
        }
    }

}
