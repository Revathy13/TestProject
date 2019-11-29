package com.org.crypto.asset.manager.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import com.org.cypto.asset.manager.CryptoAssetManager;

import junit.framework.Assert;

public class CryptoAssetManagerTest {

	@Test
	public void testJsonMapConversion() {
		System.out.println("Inside testJsonMapConversion()");
		String jsonInput = "{\"" + CryptoAssetManager.OUTPUT_CURRENCY + "\":200}";
		Map<String, String> jsonMap = CryptoAssetManager.convertJSONTOMap(jsonInput);
		assertEquals("200", jsonMap.get(CryptoAssetManager.OUTPUT_CURRENCY));
	}

	@Test
	public void testEmptyJson() {
		System.out.println("Inside testEmptyJson()");
		String jsonInput = "";
		Map<String, String> jsonMap = CryptoAssetManager.convertJSONTOMap(jsonInput);
		assertNull(jsonMap.get(CryptoAssetManager.OUTPUT_CURRENCY));
	}

	@Test
	public void testInvalidJson() {
		System.out.println("Inside testInvalidJson()");
		String jsonInput = "INvalidJsonString";
		Map<String, String> jsonMap = CryptoAssetManager.convertJSONTOMap(jsonInput);
		assertNull(jsonMap.get(CryptoAssetManager.OUTPUT_CURRENCY));
	}

	@Test
	public void testInvalidCurrency() {
		System.out.println("Inside testInvalidJson()");
		String jsonInput = "INvalidJsonString";
		Map<String, String> jsonMap = CryptoAssetManager.convertJSONTOMap(jsonInput);
		assertNull(jsonMap.get("INVALID_CURRENCY"));
	}
	
	@Test
	public void testFileExist() throws FileNotFoundException, IOException {
		System.out.println("Inside checkFileExist()");
		Properties loadInputFile = CryptoAssetManager.loadInputFile(new File(CryptoAssetManager.INPUT_FILE_NAME));
		Assert.assertNotNull(loadInputFile);
	}
}
