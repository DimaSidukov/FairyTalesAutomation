import { Low } from 'lowdb';
import { JSONFile } from 'lowdb/node';


const adapter = new JSONFile('db.json');
const db = new Low(adapter, {
    defaultData: { users: [], assignments: [] }
    });


const initDB = async () => {
     await db.read();

     await db.write();
   };

export { db, initDB };
