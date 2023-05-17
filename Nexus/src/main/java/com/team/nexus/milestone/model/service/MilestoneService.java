package com.team.nexus.milestone.model.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONObject;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.team.nexus.issue.model.dao.IssueDao;
import com.team.nexus.issue.model.service.IssueService;
import com.team.nexus.issue.model.vo.GitIssue;
import com.team.nexus.member.model.vo.Member;
import com.team.nexus.milestone.model.dao.MilestoneDao;
import com.team.nexus.milestone.model.vo.GitMilestone;
import com.team.nexus.milestone.model.vo.Milestone;

@Service
public class MilestoneService {
	
	@Autowired
	private SqlSessionTemplate sqlSession;

	@Autowired
	private WebClient webClient;

	@Autowired
	private MilestoneService mService;
	
	@Autowired
	private MilestoneDao mDao;
	
	// 이외의 get, put, delete시 사용될 동기 메서드
		public String synHttpRequest(String path, HttpSession session, String method) {
			String token = ((Member) session.getAttribute("loginUser")).getToken();

			WebClient client = WebClient.builder().baseUrl("https://api.github.com/repos/")
					.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
					.defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
					.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();

			String response = null;

			if (method.equals("get")) {
				response = client.get().uri(path).retrieve().bodyToMono(String.class).block();
			} else if (method.equals("put")) {
				response = client.put().uri(path).retrieve().bodyToMono(String.class).block();
			} else if (method.equals("delete")) {
				response = client.delete().uri(path).retrieve().bodyToMono(String.class).block();
			}

			return response;
		}

	
	public String gitPatchMethod(String path, HttpSession session, String title, String description, int mno) {

//		RestTemplate restTemplate = new RestTemplate();
//		 PATCH 메소드를 사용하기 위해 필요한 HttpClient 버전이 낮기 때문에 발생하는 문제

		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(HttpClients.createDefault());
		RestTemplate restTemplate = new RestTemplate(requestFactory);

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth((((Member) (session.getAttribute("loginUser"))).getToken()));
		headers.setContentType(MediaType.APPLICATION_JSON);
		String url = "https://api.github.com/repos/OWNER/REPO/milestones" + path + "/" + mno;

		System.out.println(url);

		// Create a JSON object for the issue payload
		JSONObject milestoneJson = new JSONObject();
		milestoneJson.put("title", title);
		milestoneJson.put("description", description);

		HttpEntity<String> entity = new HttpEntity<String>(milestoneJson.toString(), headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PATCH, entity, String.class);

		return response.getBody();
	}
	
	
	public List<GitMilestone> getMilestones(HttpSession session, String repository, String token1, String state)
			throws IOException {

	
		
		String repository2 = (String) session.getAttribute("repository");
		
		String url = "https://api.github.com/repos/" + repository + "/milestones";
		

		URL requestUrl = new URL(url);

		HttpURLConnection urlConnection = (HttpURLConnection) requestUrl.openConnection();

		urlConnection.setRequestProperty("Authorization", "Bearer " + token1);
		urlConnection.setRequestMethod("GET");

		BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

		String line;
		String responseText = "";

		while ((line = br.readLine()) != null) {
			responseText += line;
			
		}

//		System.out.println(responseText);
		JsonElement arr = (JsonElement) JsonParser.parseString(responseText);
		ArrayList<GitMilestone> list = new ArrayList<GitMilestone>();
		System.out.println(arr);
		

		/*
		 * for (int i = 0; i < arr.size(); i++) { JsonObject milestoneObj =
		 * arr.get(i).getAsJsonObject(); GitMilestone git =
		 * createGitMilestoneFromJsonObject(milestoneObj); list.add(git); }
		 */
		return list;
	}
	
	
	private GitMilestone createGitMilestoneFromJsonObject(JsonObject milestoneObj) {
//		title  creator  state  number login  avatar_url  created_at  updated_at  closed_at  due_on  id
		GitMilestone git = new GitMilestone();

		git.setTitle(milestoneObj.get("title").getAsString());
		
		/*
		 * JsonElement creatorElem = milestoneObj.get("creator"); if
		 * (!creatorElem.isJsonNull()) { JsonObject creatorObj =
		 * creatorElem.getAsJsonObject();
		 * git.setCreator(creatorObj.get("id").getAsString()); }
		 */

		git.setState(milestoneObj.get("state").getAsString());


		git.setNumber(milestoneObj.get("number").getAsInt());
		

		String createdDateTimeString = milestoneObj.get("created_at").getAsString();
		LocalDateTime createdDateTime = LocalDateTime.parse(createdDateTimeString, DateTimeFormatter.ISO_DATE_TIME);
		String createdDateString = createdDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		git.setCreatedAt(createdDateString);

		git.setUpdatedAt(milestoneObj.get("updated_at").getAsString());
		
		git.setUpdatedAt(milestoneObj.get("due_on").getAsString());

		JsonElement closedAtElem = milestoneObj.get("closed_at");
		if (!closedAtElem.isJsonNull()) {
			git.setClosedAt(closedAtElem.getAsString());
		}

		git.setMilestoneId(milestoneObj.get("id").getAsString());

		JsonObject creatorObj = milestoneObj.get("creator").getAsJsonObject();
		git.setUser(creatorObj.get("login").getAsString());

		String userProfileUrl = creatorObj.get("avatar_url").getAsString();
		git.setProfile(userProfileUrl);

		return git;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
