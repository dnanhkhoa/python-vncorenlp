package vncorenlp;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.InvalidParameterException;
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
	private final static String WORDSEGMENTER = "wseg";
	private final static String POSTAGGER = "pos";
	private final static String NERRECOGNIZER = "ner";
	private final static String DEPENDENCYPARSER = "parse";

	private final static String WORDSEGMENTER_CLASS = "vn.corenlp.wordsegmenter.WordSegmenter";
	private final static String WORDSEGMENTER_INITIALIZE_METHOD = "initialize";

	private final static String POSTAGGER_CLASS = "vn.corenlp.postagger.PosTagger";
	private final static String POSTAGGER_INITIALIZE_METHOD = "initialize";

	private final static String NERRECOGNIZER_CLASS = "vn.corenlp.ner.NerRecognizer";
	private final static String NERRECOGNIZER_INITIALIZE_METHOD = "initialize";

	private final static String DEPENDENCYPARSER_CLASS = "vn.corenlp.parser.DependencyParser";
	private final static String DEPENDENCYPARSER_INITIALIZE_METHOD = "initialize";

	private final static String TOKENIZER_CLASS = "vn.corenlp.tokenizer.Tokenizer";
	private final static String TOKENIZER_TOKENIZE_METHOD = "tokenize";
	private final static String TOKENIZER_JOINSENTENCES_METHOD = "joinSentences";

	private final static String UTILS_CLASS = "vn.pipeline.Utils";
	private final static String UTILS_DETECTLANGUAGE_METHOD = "detectLanguage";

	private final static String SENTENCE_CLASS = "vn.pipeline.Sentence";
	private final static String SENTENCE_GETWORDS_METHOD = "getWords";

	private static URLClassLoader classLoader = null;

	private static Object WordSegmenter = null;
	private static Object PosTagger = null;
	private static Object NerRecognizer = null;
	private static Object DependencyParser = null;

	private static Method detectLanguage = null;

	private static Class<?> sentenceClass = null;
	private static Method getWords = null;

	private static List<String> annotators = Arrays.asList(WORDSEGMENTER, POSTAGGER, NERRECOGNIZER, DEPENDENCYPARSER);

	// For VnCoreNLP Server
	private final static String USER_DIR = System.getProperty("user.dir");

	private final static Logger LOGGER = LoggerFactory.getLogger(VnCoreNLPServer.class);

	private final static Gson GSON = new Gson();

	private static String host = "127.0.0.1";
	private static int port = 9000;

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
			vnCoreNLPClass = classLoader.loadClass(VNCORENLP_CLASS);
			annotationClass = classLoader.loadClass(ANNOTATION_CLASS);
			sentenceClass = classLoader.loadClass(SENTENCE_CLASS);

			// VnCoreNLP class
			annotate = vnCoreNLPClass.getMethod(VNCORENLP_ANNOTATE_METHOD, annotationClass);

			// Annotation class
			getAnnotation = annotationClass.getConstructor(String.class);
			detectLanguage = annotationClass.getMethod(ANNOTATION_DETECTLANGUAGE_METHOD);
			getSentences = annotationClass.getMethod(ANNOTATION_GETSENTENCES_METHOD);

			// Sentence class
			getWords = sentenceClass.getMethod(SENTENCE_GETWORDS_METHOD);

			// Initiate the VnCoreNLP instance
			Constructor<?> constructor = vnCoreNLPClass.getConstructor(String[].class);
			vnCoreNLPInstance = constructor.newInstance(new Object[] { annotators.toArray() });

			// Get fields corresponding to each annotator

			// OK
			LOGGER.info("Loading VnCoreNLP ... OK");
			LOGGER.info("Annotators: " + String.join(", ", annotators));
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
		options.addOption(Option.builder("a").longOpt("annotators").hasArg().argName("annotators").desc(String
				.format("The annotators to run over a given sentence (default: \"%s\")", String.join(",", annotators)))
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
				String[] usedAnnotators = commandLine.getOptionValue("annotators", String.join(",", annotators)).trim()
						.split("\\s*,\\s*");
				for (String usedAnnotator : usedAnnotators) {
					if (!annotators.contains(usedAnnotator)) {
						throw new InvalidParameterException(
								String.format("Annotator \"%s\" is invalid.", usedAnnotator));
					}
				}

				annotators = Arrays.asList(usedAnnotators);

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
				Spark.get("/annotators", (request, response) -> annotators, GSON::toJson);
				Spark.post("/handle", (request, response) -> handle(request), GSON::toJson);

				// For cleaning up automatically
				Runtime.getRuntime().addShutdownHook(new Thread(() -> cleanup()));

				LOGGER.info(String.format("VnCoreNLP Server is listening on http://%s:%d", host, port));
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
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		LOGGER.info("VnCoreNLPServer is done cleaning up.");
	}

	public static String index() {
		return "VnCoreNLPServer is running.";
	}

	private static List<Object> annotate(String text) {
		return null;
	}

	private static Map<String, Object> parse(String text, String props) {
		if (props == null)
			props = String.join(",", annotators);

		Map<String, Object> annotatedText = new HashMap<>();
		try {
			// Parse annotators
			for (String annotator : props.trim().split("\\s*,\\s*")) {
				((Field) vnCoreNLPFields.get(annotator)).set(vnCoreNLPInstance, vnCoreNLPFields.get(annotator + ".o"));
			}

			// Annotate text
			Object annotation = getAnnotation.newInstance(text);
			annotate.invoke(vnCoreNLPInstance, annotation);

			// Detect language
			annotatedText.put("lang", detectLanguage.invoke(annotation));

			// Get sentences
			List<Object> annotatedSentences = new ArrayList<>();
			List<?> sentences = (List<?>) getSentences.invoke(annotation);
			for (Object sentence : sentences) {
				annotatedSentences.add(getWords.invoke(sentence));
			}
			annotatedText.put("sentences", annotatedSentences);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			for (String annotator : annotators) {
				try {
					((Field) vnCoreNLPFields.get(annotator)).set(vnCoreNLPInstance, null);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
		return annotatedText;
	}

	public static Map<String, Object> handle(Request request) {
		Map<String, Object> response = new HashMap<>();
		try {
			String text = request.queryParams("text");
			String props = request.queryParams("props");
			if (text == null) {
				throw new InvalidParameterException("Text must not be null.");
			}
			response.put("result", parse(text, props));
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
