package com.greendata.OrganizationInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.greendata.OrganizationInfo.domain.Organization;
import com.greendata.OrganizationInfo.jobs.EgrulTask;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EgrulParsingTests {
	@Test
	public void ParsingOrganizationTests()
			throws ParserConfigurationException, SAXException, IOException {

		String filename =
				"./src/test/java/com/greendata/OrganizationInfo/testEgrulXml/EGRUL_2022-12-05_897260.XML";
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = builder.parse(new File(filename));
		document.getDocumentElement().normalize();
		NodeList nList = document.getElementsByTagName("СвЮЛ");
		Node node = nList.item(0);
		Organization organization = EgrulTask.GetOrganizationFromNode(node);

		assertEquals("1156658078587", organization.getOgrn());
		assertEquals("6685101022", organization.getInn());
		assertEquals("668501001", organization.getKpp());
		assertEquals("ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ \"ЮКА\"", organization.getName());
		assertEquals("ООО \"ЮКА\"", organization.getShortName());
		assertEquals("2015-10-22", organization.getRegDate());
		assertEquals("620060,  Свердловская область, Г. ЕКАТЕРИНБУРГ, ПР-Д ГОРНИСТОВ, Д. 1Б", organization.getAddress());
	}

}
