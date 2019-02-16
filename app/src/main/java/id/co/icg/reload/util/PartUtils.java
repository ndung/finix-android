package id.co.icg.reload.util;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PartUtils {

    public static MultipartBody.Part prepareFilePart(String partName, File file) {

        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("*"),
                        file
                );

        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

}

