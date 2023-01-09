package com.greendata.OrganizationInfo.jobs;


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.greendata.OrganizationInfo.domain.Address;
import com.greendata.OrganizationInfo.domain.Organization;
import com.greendata.OrganizationInfo.repository.OrganizationRepository;
import com.greendata.OrganizationInfo.util.FileUtils;

import reactor.core.publisher.Flux;


@Component
public class EgrulTask {

    @Autowired
    WebClient webClient;
	
	@Autowired
    OrganizationRepository organizationRepository;

    // @Scheduled(cron = "@daily")
	@Scheduled(fixedDelay = 5000)
	public void updateActualEgrul() throws ParseException, IOException, ParserConfigurationException, SAXException {
		// функция будет запускаться каждый день, делать запрос к сервису https://egrul.itsoft.ru
        // и добавлять актуальную информацию в базе

		// Date currentDate = new Date();
		String dateString = "05.12.2022";
		SimpleDateFormat endpointDateFormat = new SimpleDateFormat("dd.MM.yyyy");
		Date currentDate = endpointDateFormat.parse(dateString);

		try {
			String folderPath = downloadEgrulByDate(currentDate);

			for (String filename : FileUtils.listFiles(folderPath)) {
				Path path = Paths.get(folderPath, filename);
				SaveOrganizationsFromXml(path.toString());
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private String downloadEgrulByDate(Date date) throws ParseException{
        SimpleDateFormat endpointDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String endpointDate = endpointDateFormat.format(date);
        SimpleDateFormat archiveDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String archiveDate = archiveDateFormat.format(date);

		// скачивание
		String uri = String.format("/EGRUL_406/%s/EGRUL_%s_1.zip", endpointDate, archiveDate);
		Flux<DataBuffer> dataBuffer = webClient.get()
										.uri(uri)
										.retrieve()
										.bodyToFlux(DataBuffer.class);

		// сохраниение архива
		String archivePath = String.format("src/main/java/com/greendata/OrganizationInfo/EGRUL/archive/EGRUL_%s_1.zip", archiveDate);
		Path destination = Paths.get(archivePath);
		DataBufferUtils.write(dataBuffer, destination,
			StandardOpenOption.CREATE)
			.share().block();

		// разархивирование
		String folderPath = String.format("src/main/java/com/greendata/OrganizationInfo/EGRUL/EGRUL_%s_1", archiveDate);
		FileUtils.unzipFiles(archivePath, folderPath);

		return folderPath;
	}

	private void SaveOrganizationsFromXml(String filename) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = builder.parse(new File(filename));
		document.getDocumentElement().normalize();
		
		Organization organization = null;
		
		NodeList nList = document.getElementsByTagName("СвЮЛ");
		for (int ind = 0; ind < nList.getLength(); ind++)
		{
			Node node = nList.item(ind);
			if (node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element orgElement = (Element) node;
				organization = new Organization();
				organization.setInn(orgElement.getAttribute("ИНН"));
				organization.setKpp(orgElement.getAttribute("КПП"));
				organization.setOgrn(orgElement.getAttribute("ОГРН"));
				organization.setRegDate(orgElement.getAttribute("ДатаОГРН"));
				// название
				Element nameElement = (Element) orgElement.getElementsByTagName("СвНаимЮЛ").item(0);
				organization.setName(nameElement.getAttribute("НаимЮЛПолн"));
				NodeList shortNameNode = nameElement.getElementsByTagName("СвНаимЮЛСокр");
				if (shortNameNode.getLength() > 0)
					organization.setShortName(((Element) shortNameNode.item(0)).getAttribute("НаимСокр"));
				else
					organization.setShortName("");
				// адрес
				Element addressElement = (Element) orgElement.getElementsByTagName("СвАдресЮЛ").item(0);
				NodeList addressOldFormatNode = addressElement.getElementsByTagName("АдресРФ");
				NodeList addressNewFormatNode = addressElement.getElementsByTagName("СвАдрЮЛФИАС");
				Address address = getAddressFromNodeList(addressOldFormatNode);
				if (address != null)
					organization.setAddress(address.toString());
				else
					organization.setAddress("");

				try {
					saveOrganization(organization);
				}
				catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}

	private Address getAddressFromNodeList(NodeList addressNode){
		if (addressNode.getLength() == 0)
			return null;

		Address address = new Address();
		
		Element addressElement = (Element) addressNode.item(0);
		address.setIndex(addressElement.getAttribute("Индекс"));
		if (addressElement.getElementsByTagName("Регион").getLength() > 0){
			Element regionElement = (Element) addressElement.getElementsByTagName("Регион").item(0);
			address.setRegion(regionElement.getAttribute("НаимРегион"));
			address.setRegionType(regionElement.getAttribute("ТипРегион"));
		}
		if (addressElement.getElementsByTagName("Район").getLength() > 0){
			Element districtElement = (Element) addressElement.getElementsByTagName("Район").item(0);
			address.setDistrict(districtElement.getAttribute("НаимРайон"));
			address.setDistrictType(districtElement.getAttribute("ТипРайон"));
		}
		if (addressElement.getElementsByTagName("Город").getLength() > 0){
			Element settlementElement = (Element) addressElement.getElementsByTagName("Город").item(0);
			address.setSettlement(settlementElement.getAttribute("НаимГород"));
			address.setSettlementType(settlementElement.getAttribute("ТипГород"));
		}
		if (addressElement.getElementsByTagName("НаселПункт").getLength() > 0){
			Element settlementElement = (Element) addressElement.getElementsByTagName("НаселПункт").item(0);
			address.setSettlement(settlementElement.getAttribute("НаимНаселПункт"));
			address.setSettlementType(settlementElement.getAttribute("ТипНаселПункт"));
		}
		if (addressElement.getElementsByTagName("Улица").getLength() > 0){
			Element streetElement = (Element) addressElement.getElementsByTagName("Улица").item(0);
			address.setStreet(streetElement.getAttribute("НаимУлица"));
			address.setStreetType(streetElement.getAttribute("ТипУлица"));
		}
		address.setHouse(addressElement.getAttribute("Дом"));
		address.setBuilding(addressElement.getAttribute("Корпус"));
		address.setRoom(addressElement.getAttribute("Кварт"));

		return address;
	}

    private Organization saveOrganization(Organization savedOrganization) {
        Organization organization = organizationRepository.findByInn(savedOrganization.getInn());

        if(organization != null) {
			organization.setOgrn(savedOrganization.getOgrn());
			organization.setKpp(savedOrganization.getKpp());
			organization.setName(savedOrganization.getName());
			organization.setShortName(savedOrganization.getShortName());
			organization.setAddress(savedOrganization.getAddress());
			organization.setRegDate(savedOrganization.getRegDate());
        } else {
            organization = new Organization();
			organization.setInn(savedOrganization.getInn());
			organization.setOgrn(savedOrganization.getOgrn());
			organization.setKpp(savedOrganization.getKpp());
			organization.setName(savedOrganization.getName());
			organization.setShortName(savedOrganization.getShortName());
			organization.setAddress(savedOrganization.getAddress());
			organization.setRegDate(savedOrganization.getRegDate());
        }
        return organizationRepository.save(organization);
    }
}