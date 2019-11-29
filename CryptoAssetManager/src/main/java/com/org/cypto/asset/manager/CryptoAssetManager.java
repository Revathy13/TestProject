package com.org.cypto.asset.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

public class CryptoAssetManager {
	/**
	 * Input File Name - defaults to bobs_crypto.txt
	 */
	public static final String INPUT_FILE_NAME = "bobs_crypto.txt";
	/**
	 * Currency Output - All Asset value would be shown as per below Value
	 */
	public static final Object OUTPUT_CURRENCY = "EUR";
	/**
	 * REST API - End point Template
	 */
	public static final String CRYPTO_CONVERT_ENDPOINT_TEMPLATE = "https://min-api.cryptocompare.com/data/price?fsym=%s&tsyms=%s";
	/**
	 * Logger - Externalize logging capability
	 */
	static Logger LOGGER = Logger.getLogger(CryptoAssetManager.class.getName());
	/**
	 * This attribute is used to calculate total Investment Value
	 */
	static Double totalInvestValue = 0.00;

	public static void main(String[] args) {
		try {
			// Load the data from the Input File
			Properties inputProperties = loadInputFile(new File(INPUT_FILE_NAME));
			System.out.println("File located succesfully : " + INPUT_FILE_NAME);
			System.out.println("** All rates are shown in Currency :" + OUTPUT_CURRENCY + "**");

			// Iterate through the loaded input data
			Set<Object> investmentSet = inputProperties.keySet();
			for (Object investment : investmentSet) {
				// Store the currency Type
				String currencyType = investment.toString();
				// Store the value procured by the investor
				String currencyValue = inputProperties.getProperty(investment.toString());
				/*
				 * Get the exchange Rate value Using the currency Type and
				 * Currency Value, Call the REST service Pass required values to
				 * invoke the service Returns the exchange rate on
				 * value(OUTPUT_CURRENCY)
				 */
				String exchangeRate = convertJSONTOMap(callREST(currencyType)).get(OUTPUT_CURRENCY);
				if (exchangeRate != null) {
					System.out
							.println(currencyType + "=" + String.format("%.2f", getValue(currencyValue, exchangeRate)));
					// Add the current asset value to total investment Variable
					totalInvestValue += getValue(currencyValue, exchangeRate);
				} else {
					System.err.println("Unexpected Error While converting CRYPTO symbol: " + currencyType);
				}
			}
			System.out.println("*Total Value=" + String.format("%.2f", totalInvestValue));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param inputFile
	 *            - Input File used to calculate asset value
	 * @return the loaded properties(Utility Class)
	 * @throws FileNotFoundException
	 *             - Exception thrown if unable to locate file
	 * @throws IOException
	 *             - Exception thrown if any errors occurred during reading file
	 */
	public static Properties loadInputFile(File inputFile) throws FileNotFoundException, IOException {
		Properties inputProperties = new Properties();
		InputStream stream = new FileInputStream(inputFile);
		inputProperties.load(stream);
		return inputProperties;
	}

	public static Double getValue(String currencyValue, String exchangeRate) {
		return Double.parseDouble(currencyValue) * Double.parseDouble(exchangeRate);
	}

	/**
	 * This method is used to call REST end point and get the exchange rate
	 * @param sourceCurrency
	 *            - Source CRYPTO currency format
	 * @return the REST output(JSON format)
	 */
	public static String callREST(String sourceCurrency) {
		StringBuilder restOutput = new StringBuilder();
		try {
			URL url = new URL(String.format(CRYPTO_CONVERT_ENDPOINT_TEMPLATE, sourceCurrency, OUTPUT_CURRENCY));
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setDoOutput(true);
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
				reader.lines().forEach((line) -> restOutput.append(line));
			} catch (IOException e) {
				System.err.println(e);
			}
		} catch (ProtocolException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}
		return restOutput.toString();
	}

	/**
	 * This method is used to split JSON string key/value pair to Java
	 * Collection(MAP)
	 * 
	 * @param jsonOutput
	 *            - REST end point output
	 * @return The JSON string as key/value(java.util.Map)
	 */
	public static Map<String, String> convertJSONTOMap(String jsonOutput) {
		String jsonOutputExcludingBraces = jsonOutput.replaceAll("[{}]", "");
		Map<String, String> jsonMap = new HashMap<String, String>();
		for (String keyValue : jsonOutputExcludingBraces.split(" *, *")) {
			String[] pairs = keyValue.split(" *: *", 2);
			jsonMap.put(pairs[0].trim().replaceAll("[\"\']", ""),
					pairs.length == 1 ? "" : pairs[1].trim().replaceAll("[\"\']", ""));
		}
		return jsonMap;
	}
}
