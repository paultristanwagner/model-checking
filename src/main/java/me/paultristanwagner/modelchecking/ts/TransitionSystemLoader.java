package me.paultristanwagner.modelchecking.ts;

import static me.paultristanwagner.modelchecking.util.GsonUtil.GSON;

import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TransitionSystemLoader {

  public static BasicTransitionSystem load(String path) throws IOException {
    File file = new File(path);
    return load(file);
  }

  public static BasicTransitionSystem load(File file) throws IOException {
    String fileContent = new String(Files.readAllBytes(file.toPath()));
    return fromJson(fileContent);
  }

  public static BasicTransitionSystem fromJson(String string) {
    return GSON.fromJson(string, new TypeToken<BasicTransitionSystem>() {}.getType());
  }
}
