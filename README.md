<h1 align="center">🍄<strong> MapleStory Chatbot </strong>🍄</h1>

<div align="center">
  <em>카카오톡 메이플스토리 공지사항 챗봇</em>
  <br><br>
  https://pf.kakao.com/_mAsQG
</div>

<br>

## Description
- 모바일 환경에서 메이플스토리 소식을 확인하려면 메이플스토리 공식 사이트에 접속한 후, 뉴스 섹션을 클릭하고, 원하는 섹션을 클릭하고, 원하는 소식을 클릭해야 하는 번거로운 과정이 필요합니다. <br>
- 또한 공식 사이트가 모바일 버전 최적화가 되어 있지 않아 불편함이 더욱 크다고 느꼈습니다. <br>
- 이를 해결하기 위해 NEXON Open API를 활용하여 공지사항, 클라이언트 업데이트 등 메이플스토리의 다양한 최신 정보를 간편하게 확인할 수 있는 카카오톡 챗봇을 개발하여 서비스 중입니다.

<br>

## Architecture
<img width="600" height="360" src="https://github.com/user-attachments/assets/ba6f2e26-4d5e-4c6e-ae07-69039067219b"> <br>

<br>

## Tech Stack
<div>
    <table>
        <tr>
            <td colspan="2" align="center">
                Language
            </td>
            <td colspan="4">
                <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=openjdk&logoColor=white">
            </td>
        </tr>
        <tr>
            <td colspan="2" align="center">
                Framework
            </td>
            <td colspan="4">
                <img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
                <img src="https://img.shields.io/badge/Spring Batch-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
            </td>
        </tr>
        <tr>
            <td colspan="2" align="center">
                API
            </td>
            <td colspan="4">
                <img src="https://img.shields.io/badge/NEXON Open API-0054A3?style=for-the-badge&logo=nginx&logoColor=white"> 
            </td>
        </tr>
        <tr>
            <td colspan="2" align="center">
                Server
            </td>
            <td colspan="4">
                <img src="https://img.shields.io/badge/amazon ec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white"> 
            </td>
        </tr>
        <tr>
            <td colspan="2" align="center">
                Database
            </td>
            <td colspan="4">
                <img src="https://img.shields.io/badge/Amazon RDS-527FFF?style=for-the-badge&logo=Amazon RDS&logoColor=white">
                <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white">
            </td>
        </tr>
        <tr>
            <td colspan="2" align="center">
                Tool
            </td>
            <td colspan="4">
                <img src="https://img.shields.io/badge/IntelliJ IDEA-000000?style=for-the-badge&logo=intellijidea&logoColor=white">
            </td>
        </tr>
        <tr>
            <td colspan="2" align="center">
                etc.
            </td>
            <td colspan="4">
                <img src="https://img.shields.io/badge/KakaoTalk-FFCD00?style=for-the-badge&logo=KakaoTalk&logoColor=black">
                <img src="https://img.shields.io/badge/FileZilla-BF0000?style=for-the-badge&logo=FileZilla&logoColor=white">
                <img src="https://img.shields.io/badge/postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white">
            </td>
        </tr>
    </table>
</div>

<br>

## Screenshots

<table>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/56b25cf4-592a-42d8-80ec-e499e599ee95" width="300" height="650"><p>기본 메시지</p></td>
    <td><img src="https://github.com/user-attachments/assets/58c9d89d-2d63-4438-8273-49524fdb2c40" height="650"><p>공지사항 확인 기능</p></td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/33069bc7-9fcd-4ef6-8249-19bc618ea721" width="300" height="650"><p>클라이언트 업데이트 확인 기능</p></td>
    <td><img src="https://github.com/user-attachments/assets/588d7d01-fb74-4e1c-af7d-30a60d751c71" width="300" height="650"><p>진행 중 이벤트 확인 기능</p></td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/67676158-3c02-4206-a723-f7162dc3bcb7" width="300" height="650"><p>캐시샵 공지 확인 기능</p></td>
  </tr>
</table>

<br>

## How it works?
1. 서버는 최초 실행 시 NEXON Open API를 호출하여 공지사항, 클라이언트 업데이트, 진행 중 이벤트, 캐시샵 공지 데이터를 요청합니다.

2. 데이터를 Amazon RDS DB (MySQL)에 저장합니다.

3. 카카오톡 챗봇의 요청에 해당하는 데이터를 JSON 형식으로 응답합니다.
- Ehcache3를 활용하여 캐싱을 구현하였습니다.
- 캐싱 적용 전 데이터 조회 소요 시간: 약 250 ~ 300ms
- 캐싱 적용 후 데이터 조회 소요 시간: 약 1 ~ 2ms

4. 매일 오전 03:00에 NEXON Open API를 호출하여 데이터를 갱신합니다.
- Spring Batch를 활용하여 병렬 처리를 구현하였습니다.
- 병렬 처리 적용 전 데이터 갱신 소요 시간: 1000 ~ 1100ms
- 병렬 처리 적용 후 데이터 갱신 소요 시간: 400 ~ 500ms

<br>
