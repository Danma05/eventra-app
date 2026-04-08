require("dotenv").config(); // importante

const app = require('./src/app');

const PORT = process.env.PORT || 3002;

app.listen(PORT, () => {
  console.log(`User service running on port ${PORT}`);
});