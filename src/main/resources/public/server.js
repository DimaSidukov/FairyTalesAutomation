import express from 'express';
import bodyParser from 'body-parser';
import cors from 'cors';
import { db, initDB } from './database.js';

const app = express();

app.use(bodyParser.json());
app.use(cors());

// Define your routes
const setupRoutes = () => {
  app.delete('/users/:id', async (req, res) => {
    const userId = req.params.id;

    try {
      // Delete user
      db.data.users = db.data.users.filter(user => user.id !== userId);

      // Delete related assignments and other data
      db.data.assignments = db.data.assignments.filter(
        assignment => assignment.participantId !== userId
      );

      await db.write();

      res.status(204).send();
    } catch (error) {
      console.error('Error deleting user data:', error);
      res.status(500).send('Error deleting user data');
    }
  });

  // Add other routes here as needed
};

// Start the server after initializing the database
const startServer = async () => {
  await initDB(); // Initialize the database
  setupRoutes();  // Set up routes after the database is ready

  const PORT = process.env.PORT || 3001;
  app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
  });
};

startServer();
