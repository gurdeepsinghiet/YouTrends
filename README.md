<div align="center">
  <a href="http://telegram.me/YouTrendsBot">
    <img  src="https://cdn.worldvectorlogo.com/logos/youtube-2-1.svg" width="64"/>
  </a>
  <a href="http://telegram.me/YouTrendsBot">
    <img src="https://telegram.org/img/t_logo.png" width="32"/>
  </a>
  <h1>
    <a href="http://telegram.me/YouTrendsBot")>@YouTrendsBot</a><br>
    YouTube Trending REST API<br>
    YouTube Trending analyse instruments<br>
    Daily distribution of YouTube trends into telegram<br>
    Free access now!!!
  </h1>
</div>
<h2>Main features:<br></h2>
- access to YouTube trends feed by our REST API;<br>
- access to analytics info by REST API;<br>
- dispatch of trends feed every day;<br>
- opportunity see top channels by conut hours in trends by last week;<br>
- opportunity see top channels by conut videos in trends by last week;<br>
- every day feed include most frequent words in video titles by last week;<br>
- filtering by channels and tags in the title;<br>
- only videos not older than 24 hours fall into feed;<br>
- sort by count of views;<br>
- feed contains 10 videos (for you after filtration).<br>
<h2>YouTube Trending REST API</h2>
<b>You can get YouTube Trending feed for FREE on www.youtrends.org:8080</b>
<h3>Methods</h3>
<h4>1) Trending.</h4>
This is main method. You can get feed from YouTube Trending.<br>
Example request:<br>
http://www.youtrends.org:8080/trends<br>
Response:<br>
Array of Video.<br>
<h4>2) Popular words.</h4>
Return popular word in video titles by last day.<br>
Example request:<br>
http://www.youtrends.org:8080/popularWords<br>
Response:<br>
Array of top popular words with count entries by last day<br>
<h4>3) Top channels by unique videos in trending by last week.</h4>
Example request:<br>
http://www.youtrends.org:8080/topByVideosCount<br>
Response:<br>
Array of top channels with count videos by last week<br>
<h4>4) Top channels by count hours in trending by last week.</h4>
Example request:<br>
http://www.youtrends.org:8080/topByHoursCount<br>
Response:<br>
Array of top channels with count hours in trending by last week<br>

