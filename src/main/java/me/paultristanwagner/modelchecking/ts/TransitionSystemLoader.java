package me.paultristanwagner.modelchecking.ts;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TransitionSystemLoader {

  private static final Gson GSON;

  static {
    GSON =
        new GsonBuilder()
            .registerTypeAdapter(TSTransition.class, new TSTransition.TSTransitionAdapter())
            .setPrettyPrinting()
            .create();
  }

  public static TransitionSystem load(String path) throws IOException {
    File file = new File(path);
    return load(file);
  }

  public static TransitionSystem load(File file) throws IOException {
    String fileContent = new String(Files.readAllBytes(file.toPath()));
    return fromJson(fileContent);
  }

  public static TransitionSystem fromJson(String string) {
    return GSON.fromJson(string, TransitionSystem.class);
  }
}
