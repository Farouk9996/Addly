const { initializeTestEnvironment, assertFails, assertSucceeds } = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const assert = require("node:assert");
const fs = require("node:fs");

const PROJECT_ID = "gen-lang-client-0721620813";
const DATABASE_ID = "ai-studio-android-learnbri-bb5b367e-9b2e-4860-acc0-0a7161940e75";

let testEnv;

before(async () => {
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules: fs.readFileSync("firestore.rules", "utf8"),
      host: "127.0.0.1",
      port: 8080
    }
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("unauthenticated user cannot read or write user profile", async () => {
  const unauthedDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthedDb.collection("users").doc("user_123").get());
  await assertFails(unauthedDb.collection("users").doc("user_123").set({
    userId: "user_123",
    displayName: "Test"
  }));
});

test("authenticated user can read and write own profile", async () => {
  const authedDb = testEnv.authenticatedContext("user_123").firestore();
  await assertSucceeds(authedDb.collection("users").doc("user_123").set({
    userId: "user_123",
    displayName: "Student Mohammed",
    email: "student@example.com",
    stayLoggedIn: true
  }));
  await assertSucceeds(authedDb.collection("users").doc("user_123").get());
});

test("authenticated user cannot write other user's profile", async () => {
  const authedDb = testEnv.authenticatedContext("user_123").firestore();
  await assertFails(authedDb.collection("users").doc("user_456").set({
    userId: "user_456",
    displayName: "Hacker"
  }));
});

test("user can create and read own cloud bookmark", async () => {
  const authedDb = testEnv.authenticatedContext("user_123").firestore();
  await assertSucceeds(authedDb.collection("users").doc("user_123").collection("cloud_bookmarks").doc("bm_1").set({
    userId: "user_123",
    title: "مقياس قانون الأعمال",
    url: "https://mast-da.ufc.dz/s1/my/courses.php",
    portalTitle: "جامعة التكوين المتواصل UFC",
    isFavorite: true,
    sortOrder: 1
  }));
});

test("user cannot read another user's cloud bookmark", async () => {
  const user1Db = testEnv.authenticatedContext("user_1").firestore();
  await assertSucceeds(user1Db.collection("users").doc("user_1").collection("cloud_bookmarks").doc("bm_1").set({
    userId: "user_1",
    title: "مقياس خاص",
    url: "https://mast-da.ufc.dz/s1/",
    isFavorite: true
  }));

  const user2Db = testEnv.authenticatedContext("user_2").firestore();
  await assertFails(user2Db.collection("users").doc("user_1").collection("cloud_bookmarks").doc("bm_1").get());
});
