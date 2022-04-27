package ru.netology;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class App {
    public static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        String json = gson.toJson(list, listType);
        return json;
    }
    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> list = null;
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            list = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
    public static void writeString(String json, String jsonFileName) {
        try (FileWriter fileWriter = new FileWriter(jsonFileName)) {
            fileWriter.write(json);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static List<Employee> parseXML(String xmlFileName) {
        List<Employee> list = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(xmlFileName));
            Node root = document.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node mainNode = nodeList.item(i);
                Employee employee = new Employee();
                if (Node.ELEMENT_NODE == mainNode.getNodeType()) {
                    Element element = (Element) mainNode;
                    NodeList childNodes = element.getChildNodes();
                    for (int j = 0; j < childNodes.getLength(); j++) {
                        Node node = childNodes.item(j);
                        if (Node.ELEMENT_NODE == node.getNodeType()) {
                            switch (node.getNodeName()) {
                                case "id":
                                    employee.setId(Long.parseLong(node.getTextContent()));
                                    break;
                                case "firstName":
                                    employee.setFirstName(node.getTextContent());
                                    break;
                                case "lastName":
                                    employee.setLastName(node.getTextContent());
                                    break;
                                case "country":
                                    employee.setCountry(node.getTextContent());
                                    break;
                                case "age":
                                    employee.setAge(Integer.parseInt(node.getTextContent()));
                                    break;
                            }
                        }
                    }
                    list.add(employee);
                }
            }

        } catch(ParserConfigurationException | NumberFormatException | SAXException | IOException e){
            e.printStackTrace();
        }
        return list;
    }

    public static String readString(String jsonFileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(jsonFileName))) {
            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static List<Employee> jsonToList(String json) {
        List<Employee> list = new ArrayList<>();
        JsonArray jsonArray = (JsonArray) new JsonParser().parse(json);
        for (JsonElement jsonObject : jsonArray) {
            Gson gson = new GsonBuilder().create();
            Employee employee = gson.fromJson(jsonObject, Employee.class);
            list.add(employee);
        }
        return list;
    }
    public static void main( String[] args ) {
        String [] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String csvFileName = "data.csv";
        writeString(listToJson(parseCSV(columnMapping, csvFileName)), "data.json");
        writeString(listToJson(parseXML("data.xml")), "OutputJson.json");
        jsonToList(readString("OutputJson.json")).forEach(System.out::println);
    }
}
