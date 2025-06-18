//package com.Trekkit_Java.ExternalAPI;
//
//import java.io.BufferedReader;
//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.NodeList;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
////import org.w3c.dom.NodeList;
//
//import com.Trekkit_Java.DTO.MountainDTO;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//@Component
//public class MountainAPIClient {
//	
//	@Value("${openapi.service-key}")
//	private String serviceKey;
//
//    public List<MountainDTO> callMountainAPI() {
//        List<MountainDTO> mountains = new ArrayList<>();
//        int page = 1;
//        boolean hasMore = true;
//
//        try {
//        	while (hasMore) {
//        	String serviceKey = this.serviceKey;
//        	String url = "https://apis.data.go.kr/1400000/service/cultureInfoService2/mntInfoOpenAPI2"
//                    + "?serviceKey=" + serviceKey
//                    + "&pageNo=" +page
//                    + "&numOfRows=100"
//                    + "&MobileOS=ETC"
//                    + "&MobileApp=trekkit"
//                    + "&_type=json";
//        	
//            URL apiUrl = new URL(url);
//            HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();
//            conn.setRequestMethod("GET");
//            
//            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
//            StringBuilder sb = new StringBuilder();
//            
//            String line;
//            while ((line = rd.readLine()) != null) {
//                sb.append(line);
//            }
//
//            rd.close();
//            conn.disconnect();
//            
//            System.out.println("산림청 산정보 API 응답: " + sb.toString());
//
//            ObjectMapper mapper = new ObjectMapper();
//            JsonNode root = mapper.readTree(sb.toString());
//            JsonNode items = root.path("response").path("body").path("items").path("item");
//            int totalCount = root.path("response").path("body").path("totalCount").asInt();
//            int numOfRows = root.path("response").path("body").path("numOfRows").asInt();
//
//            if (items.isArray()) {
//                for (JsonNode item : items) {
//                    MountainDTO dto = new MountainDTO();
//                    
//                    dto.setName(item.path("mntiname").asText());
//                    dto.setLocation(item.path("mntiadd").asText());
//                    dto.setHeight(item.path("mntihigh").asDouble());
//                    dto.setDetails(item.path("mntidetails").asText());
//                    dto.setMntiListNo(item.path("mntilistno").asText());
//                    String imageUrl = "/assets/images/" + dto.getName() + "/1.jpg";
//                    dto.setImageUrl(imageUrl);
//
//                    mountains.add(dto);
//                }
//            }
//
//            int totalPages = (int) Math.ceil((double) totalCount / numOfRows);
//            if (page >= totalPages) {
//                hasMore = false;
//            } else {
//                page++;
//            }
//        }
//        	
//        } catch (Exception e) {
//        	System.out.println("API 호출 중 예외 발생:");
//            e.printStackTrace();
//        }
//
//        return mountains;
//    }
//}
