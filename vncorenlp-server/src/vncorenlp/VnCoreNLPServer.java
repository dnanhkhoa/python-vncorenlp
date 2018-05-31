package vncorenlp;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import spark.Request;
import spark.Spark;

public final class VnCoreNLPServer {

    // For VnCoreNLP library
    private final static String              WORDSEGMENTER          = "wseg";
    private final static String              POSTAGGER              = "pos";
    private final static String              NERRECOGNIZER          = "ner";
    private final static String              DEPENDENCYPARSER       = "parse";
    private final static List<String>        DEFAULT_ANNOTATORS     = Arrays.asList(WORDSEGMENTER, POSTAGGER,
                                                                                    NERRECOGNIZER, DEPENDENCYPARSER);
    private final static List<String>        ANNOTATORS             = new ArrayList<>();

    private final static Map<String, Object> INITIALIZED_ANNOTATORS = new HashMap<>();

    private static URLClassLoader            classLoader            = null;
    private static Constructor<?>            getSentence            = null;
    private static Method                    tokenize               = null;
    private static Method                    joinSentences          = null;
    private static Method                    detectLanguage         = null;
    private static Method                    getWords               = null;

    // For VnCoreNLPServer
    private final static String              USER_DIR               = System.getProperty("user.dir");

    private final static Logger              LOGGER                 = LoggerFactory.getLogger(VnCoreNLPServer.class);

    private final static Gson                GSON                   = new Gson();

    private static String                    host                   = "127.0.0.1";
    private static int                       port                   = 9000;

    private static void loadVnCoreNLP(File vnCoreNLPFile) throws Exception {
        try {
            // Load VnCoreNLP library
            classLoader = new URLClassLoader(new URL[] { vnCoreNLPFile.toURI().toURL() });

            // For loading models
            String vnCoreNLPDir = vnCoreNLPFile.getParent();
            if (vnCoreNLPDir != null) {
                if (vnCoreNLPDir.endsWith("\\") || vnCoreNLPDir.endsWith("/")) {
                    vnCoreNLPDir = vnCoreNLPDir.substring(0, vnCoreNLPDir.length() - 1);
                }
                System.setProperty("user.dir", vnCoreNLPDir);
            }

            // Load classes and methods
            Class<?> wordSegmenterClass = classLoader.loadClass("vn.corenlp.wordsegmenter.WordSegmenter");
            Method initializeWordSegmenter = wordSegmenterClass.getMethod("initialize");
            if (ANNOTATORS.contains(WORDSEGMENTER)) {
                INITIALIZED_ANNOTATORS.put(WORDSEGMENTER, initializeWordSegmenter.invoke(null));
            }

            Class<?> posTaggerClass = classLoader.loadClass("vn.corenlp.postagger.PosTagger");
            Method initializePosTagger = posTaggerClass.getMethod("initialize");
            if (ANNOTATORS.contains(POSTAGGER)) {
                INITIALIZED_ANNOTATORS.put(POSTAGGER, initializePosTagger.invoke(null));
            }

            Class<?> nerRecognizerClass = classLoader.loadClass("vn.corenlp.ner.NerRecognizer");
            Method initializeNerRecognizer = nerRecognizerClass.getMethod("initialize");
            if (ANNOTATORS.contains(NERRECOGNIZER)) {
                INITIALIZED_ANNOTATORS.put(NERRECOGNIZER, initializeNerRecognizer.invoke(null));
            }

            Class<?> dependencyParserClass = classLoader.loadClass("vn.corenlp.parser.DependencyParser");
            Method initializeDependencyParser = dependencyParserClass.getMethod("initialize");
            if (ANNOTATORS.contains(DEPENDENCYPARSER)) {
                INITIALIZED_ANNOTATORS.put(DEPENDENCYPARSER, initializeDependencyParser.invoke(null));
            }

            Class<?> sentenceClass = classLoader.loadClass("vn.pipeline.Sentence");
            getSentence = sentenceClass.getConstructor(String.class, wordSegmenterClass, posTaggerClass,
                                                       nerRecognizerClass, dependencyParserClass);
            getWords = sentenceClass.getMethod("getWords");

            Class<?> tokenizerClass = classLoader.loadClass("vn.corenlp.tokenizer.Tokenizer");
            tokenize = tokenizerClass.getMethod("tokenize", String.class);
            joinSentences = tokenizerClass.getMethod("joinSentences", List.class);

            Class<?> utilsClass = classLoader.loadClass("vn.pipeline.Utils");
            detectLanguage = utilsClass.getMethod("detectLanguage", String.class);

            // Load models in first time
            annotate("SIM số đẹp tăng giá sau thông tin rút về 10 số.", ANNOTATORS.toArray(new String[0]));
        } finally {
            // Restore "user.dir" variable
            System.setProperty("user.dir", USER_DIR);
        }
    }

    private static Options buildOptions() {
        Options options = new Options();
        options.addOption(Option.builder("h").longOpt("help").desc("Show this help").build());
        options.addOption(Option.builder("i").longOpt("host").hasArg().argName("host")
                .desc(String.format("The hostname to bind the server to (default: %s)", host)).type(String.class)
                .build());
        options.addOption(Option.builder("p").longOpt("port").hasArg().argName("port")
                .desc(String.format("The port number to bind the server to (default: %s)", port)).type(int.class)
                .build());
        options.addOption(Option.builder("a").longOpt("annotators").hasArg().argName("annotators")
                .desc(String.format("The annotators to run over a given sentence (default: \"%s\")",
                                    String.join(",", DEFAULT_ANNOTATORS)))
                .type(String.class).build());
        return options;
    }

    private static void showHelpMessage(Options options) {
        // Show help message
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("java -Xmx2g -jar VnCoreNLPServer.jar <VnCoreNLP> [Options ...]", options);
    }

    public static void main(String[] args) {
        CommandLineParser commandLineParser = new DefaultParser();
        Options options = buildOptions();
        try {
            // Parse arguments
            CommandLine commandLine = commandLineParser.parse(options, args);

            if (args.length == 0 || commandLine.hasOption("help")) {
                showHelpMessage(options);
            } else {
                // Get option values
                host = commandLine.getOptionValue("host", host);
                port = Integer.parseInt(commandLine.getOptionValue("port", String.valueOf(port)));

                // Parse annotators
                String[] annotators = commandLine.getOptionValue("annotators", String.join(",", DEFAULT_ANNOTATORS))
                        .toLowerCase().trim().split("\\s*,\\s*");
                for (String annotator : annotators) {
                    if (annotator.length() > 0) {
                        if (!DEFAULT_ANNOTATORS.contains(annotator)) {
                            throw new InvalidParameterSpecException(String.format("Annotator \"%s\" is invalid.",
                                                                                  annotator));
                        }
                        ANNOTATORS.add(annotator);
                    }
                }
                LOGGER.info("Using annotators: " + String.join(", ", ANNOTATORS));

                // Load VnCoreNLP library
                File vnCoreNLPFile = new File(args[0]);
                if (!vnCoreNLPFile.isFile()) {
                    throw new FileNotFoundException(String.format("File \"%s\" was not found.", vnCoreNLPFile));
                }
                loadVnCoreNLP(vnCoreNLPFile);

                // Settings
                Spark.ipAddress(host);
                Spark.port(port);

                // Routes
                Spark.get("/", (request, response) -> index());
                Spark.get("/annotators", (request, response) -> ANNOTATORS, GSON::toJson);
                Spark.post("/handle", (request, response) -> handle(request), GSON::toJson);

                // For cleaning up automatically
                Runtime.getRuntime().addShutdownHook(new Thread(() -> cleanup()));

                LOGGER.info(String.format("VnCoreNLPServer is listening on http://%s:%d", host, port));
            }
        } catch (ParseException e) {
            showHelpMessage(options);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static void cleanup() {
        LOGGER.info("VnCoreNLPServer is cleaning up...");
        try {
            LOGGER.info("VnCoreNLPServer is closing class loader...");
            classLoader.close();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        try {
            LOGGER.info("VnCoreNLPServer is stopping Spark services...");
            Spark.stop();
            LOGGER.info("VnCoreNLPServer is done cleaning up.");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static String index() {
        return "VnCoreNLPServer is running.";
    }

    private static List<Object> annotate(String text, String[] annotators) throws Exception {
        // Set up necessary arguments
        Map<String, Object> args = new HashMap<>();
        for (String annotator : annotators) {
            if (annotator.length() > 0) {
                if (!DEFAULT_ANNOTATORS.contains(annotator)) {
                    throw new InvalidParameterSpecException(String.format("Annotator \"%s\" is invalid.", annotator));
                }
                args.put(annotator, INITIALIZED_ANNOTATORS.get(annotator));
            }
        }

        List<Object> annotatedSentences = new ArrayList<>();

        // Split text into sentences
        List<?> sentences = (List<?>) joinSentences.invoke(null, tokenize.invoke(null, text));
        for (Object sentence : sentences) {
            // Annotate each sentence
            sentence = getSentence.newInstance(sentence, args.get(WORDSEGMENTER), args.get(POSTAGGER),
                                               args.get(NERRECOGNIZER), args.get(DEPENDENCYPARSER));
            annotatedSentences.add(getWords.invoke(sentence));
        }
        return annotatedSentences;
    }

    public static Map<String, Object> handle(Request request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String text = request.queryParams("text");
            String props = request.queryParams("props");
            if (text == null) {
                throw new InvalidParameterSpecException("Text must not be null.");
            }
            if (props == null) {
                props = String.join(",", DEFAULT_ANNOTATORS);
            }

            // Normalize properties
            props = props.toLowerCase().trim();

            if (props.equals("lang")) {
                try {
                    response.put("language", detectLanguage.invoke(null, text));
                } catch (Exception ex) {
                    // Respond "No Answer" if error
                    response.put("language", "N/A");

                    // For debugging
                    LOGGER.error(ex.getMessage(), ex);
                }
            } else {
                response.put("sentences", annotate(text, props.split("\\s*,\\s*")));
            }
            response.put("status", true);
        } catch (Exception e) {
            // Respond error message
            response.put("error", e.getMessage());
            response.put("status", false);

            // For debugging
            LOGGER.error(e.getMessage(), e);
        }
        return response;
    }
}
