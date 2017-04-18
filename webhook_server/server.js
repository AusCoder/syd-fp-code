var express = require('express');
var bodyParser = require('body-parser');

var app = express();
app.use(bodyParser.json());

app.post('/results', (req, res) => {
  console.log(' [x] received results: ', req.body);
  res.sendStatus(200);
});

var PORT = 8080;
app.listen(PORT);

console.log(' [*] webhook server running on port: ' + PORT);
