package com.app.rest.client;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import static com.app.rest.client.ApacheHttpClient.basePath;
import static org.assertj.core.api.Assertions.*;

public class Main {

    public static void main(String[] args) throws IOException {
        basePath = args[0];
        ArrayList<String> filesArray = ApacheHttpClientGet.getFilesList("oldStorage");
        ArrayList<String> filesArrayNew = new ArrayList<>();
        assert filesArray != null;
        int total = filesArray.size();

        while (filesArray.size() > 0) {
            for (int i = 0; i < filesArray.size(); ++i) {
                String str = filesArray.get(i);
                System.out.print(str + " in progress ...");
                ApacheHttpClientGet.getFile(str);
                System.out.print("received ... ");
                ApacheHttpClientPost.postFile(str);
                String filePath = basePath;
                filePath += str;

                boolean resultDeletedFile = Files.deleteIfExists(Paths.get(filePath));
                //test for the successful temp file deletion
                //assertThat(resultDeletedFile).isEqualTo(true);

                System.out.print("...done\n");
                System.out.println("TOTAL OPERATIONS: " + i);
            }

            filesArrayNew = ApacheHttpClientGet.getFilesList("newStorage");

            assert filesArrayNew != null;
            for (int i = 0; i < filesArrayNew.size(); ++i) {
                String str = filesArrayNew.get(i);
                if (filesArray.contains(str)){
                    ApacheHttpClientDelete.deleteFile(str);
                    System.out.print("deleted\n");
                    System.out.println("TOTAL DELETE OPERATIONS: " + i);
                }
            }
            filesArray = ApacheHttpClientGet.getFilesList("oldStorage");

        }
        assert filesArrayNew.size() == total;
    }
}
