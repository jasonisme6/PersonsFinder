# Persons Finder - Backend Challenge

A REST API for finding people around you, featuring AI-generated biographies and MongoDB geospatial queries.

---

## 🚀 Quick Start

### Prerequisites
- **Java 11+**
- **Docker** (for MongoDB)
- **OpenAI API Key** (optional, falls back to mock service)

### 1. Start MongoDB
```bash
docker-compose up -d
```

### 2. Set OpenAI API Key (Optional)
```bash
# Linux/macOS
export OPENAI_API_KEY=sk-your-key

# Windows
set OPENAI_API_KEY=sk-your-key
```

### 3. Run Application
```bash
./gradlew bootRun
```

Application starts at: `http://localhost:8080`

### 4. Access MongoDB Console (Optional)
Open browser: `http://localhost:8081`
- Username: `admin`
- Password: `admin123`

---

## 📋 API Endpoints

### Create Person
```bash
POST /api/v1/persons
Content-Type: application/json

{
  "name": "Alice Chen",
  "jobTitle": "Software Engineer",
  "hobbies": ["hiking", "photography", "reading"],
  "latitude": 40.7128,
  "longitude": -74.0060
}
```

**Response:**
```json
{
  "id": "507f1f77bcf86cd799439011",
  "name": "Alice Chen",
  "jobTitle": "Software Engineer",
  "hobbies": ["hiking", "photography", "reading"],
  "bio": "Meet Alice, a Software Engineer who scales mountains and captures moments!"
}
```

### Update Location
```bash
PUT /api/v1/persons/{id}/location
Content-Type: application/json

{
  "latitude": 40.7589,
  "longitude": -73.9851
}
```

### Find Nearby Persons
```bash
GET /api/v1/persons/nearby?latitude=40.7128&longitude=-74.0060&radius=10
```

**Query Parameters:**
- `latitude` (required): Center latitude
- `longitude` (required): Center longitude  
- `radius` (optional, default 10.0): Search radius in kilometers

**Response:**
```json
[
  {
    "id": "507f1f77bcf86cd799439011",
    "name": "Alice Chen",
    "jobTitle": "Software Engineer",
    "hobbies": ["hiking", "photography"],
    "bio": "Meet Alice, a Software Engineer...",
    "latitude": 40.7128,
    "longitude": -74.0060,
    "distanceInKm": 0.0
  }
]
```

---

## 🗄️ Database Setup

### MongoDB Web Console

Access **Mongo Express** (web-based admin panel):

```
http://localhost:8081
```

**Credentials:**
- Username: `admin`
- Password: `admin123`

**Features:**
- Browse databases and collections
- View/edit/delete documents
- Run queries
- View indexes
- Export data

### Seed Database with Sample Data

**Seed 10,000 records:**
```bash
curl -X POST "http://localhost:8080/api/admin/seed?count=10000"
```

**Custom record count:**
```bash
curl -X POST "http://localhost:8080/api/admin/seed?count=1000"
```

**Clear all data:**
```bash
curl -X DELETE "http://localhost:8080/api/admin/clear"
```

**Check database stats:**
```bash
curl "http://localhost:8080/api/admin/stats"
```

### MongoDB Command Line (Alternative)

```bash
# Connect to MongoDB shell
docker exec -it persons-finder-mongodb mongosh -u admin -p password123

# Switch to database
use personsdb

# View collections
show collections

# Count documents
db.persons.countDocuments()

# View indexes
db.persons.getIndexes()

# Sample query
db.persons.find().limit(5)
```

### Verify Indexes
```bash
docker exec -it persons-finder-mongodb mongosh -u admin -p password123

use personsdb
db.persons.getIndexes()
```

Expected indexes:
- `_id` (default)
- `location` (2dsphere geospatial)
- `name` (ascending)

---

## 🧪 Running Tests

```bash
# All tests
./gradlew test

# Skip tests requiring OpenAI
./gradlew test -x OpenAIBioServiceTest

# Specific test
./gradlew test --tests MongoDBIntegrationTest
```

Test reports: `build/reports/tests/test/index.html`

---

## 🏗️ Architecture

```
presentation/
├── PersonController.kt          # REST endpoints
└── dto/                         # Request/Response objects

domain/services/
├── PersonsServiceMongoImpl.kt   # Person CRUD with MongoDB
├── LocationsServiceMongoImpl.kt # Geospatial queries
├── OpenAIBioService.kt          # AI bio generation
└── PromptInjectionServiceImpl.kt # Security layer

data/
├── PersonDocument.kt            # MongoDB entity
├── PersonRepository.kt          # Spring Data repository
└── Location.kt                  # Domain model

config/
├── OpenAIConfig.kt              # OpenAI client setup
├── MongoIndexConfig.kt          # Auto-create indexes
└── DataSeeder.kt                # Database seeding
```

---

## 🔒 Security Features

### Prompt Injection Defense
- **Pattern detection**: Regex for "ignore", "system", "prompt" keywords
- **LLM analysis**: OpenAI evaluates suspicious inputs
- **Character whitelisting**: Only alphanumeric + basic punctuation
- **Length limits**: 500 characters max per field

See [SECURITY.md](SECURITY.md) for detailed analysis.

---

## 🤖 AI Integration

### OpenAI GPT-4o-mini
- Bio generation based on job title + hobbies
- Temperature: 0.7 (creative but consistent)
- Max tokens: 500
- Cost: ~$0.0001 per bio

### LLM as Judge Testing
Tests use AI to evaluate AI output quality:
- Relevance, coherence, creativity scores
- Safety checks for harmful content
- Automated quality assurance

See [AI_LOG.md](AI_LOG.md) for AI collaboration details.

---

## 📊 Performance

### With Geospatial Index
| Records   | Query Time |
|-----------|------------|
| 10,000    | ~50ms      |
| 1,000,000 | ~200ms     |

### Without Index
| Records   | Query Time |
|-----------|------------|
| 10,000    | ~500ms     |
| 1,000,000 | ~10s       |

**Index provides 10-50x performance improvement.**

---

## 🛠️ Configuration

### Environment Variables
```bash
OPENAI_API_KEY=sk-your-key        # OpenAI API key (optional)
SEED_COUNT=10000                  # Records to seed (default: 10000)
```

### Application Properties
`src/main/resources/application.properties`:
```properties
server.port=8080

# MongoDB
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=personsdb
spring.data.mongodb.username=admin
spring.data.mongodb.password=password123

# OpenAI
openai.api.key=${OPENAI_API_KEY}
openai.model=gpt-4o-mini
openai.max.tokens=500
openai.temperature=0.7
```

---

## 🐛 Troubleshooting

### MongoDB connection failed
```bash
docker-compose restart
docker ps  # Verify container is running
```

### Port 8080 already in use
```bash
./gradlew bootRun --args='--server.port=9090'
```

### OpenAI API errors
Check API key:
```bash
echo $OPENAI_API_KEY
```

Application falls back to mock service if key is missing.

### Build fails
```bash
./gradlew clean build --refresh-dependencies
```

---

## 📚 Documentation

- **README.md** (this file) - How to run
- **AI_LOG.md** - AI collaboration log
- **SECURITY.md** - Security analysis and PII handling

---

## 🎯 Tech Stack

- **Kotlin** 1.6.21
- **Spring Boot** 2.7.0
- **MongoDB** 7.0 (Docker)
- **OpenAI GPT-4o-mini** via openai-client-kotlin 3.6.3
- **JUnit 5** + Testcontainers for testing

---

## 📝 License

Coding challenge project - free to use and modify.
