#!/bin/bash

# Database Seeding Script
# Seeds MongoDB with 10,000 sample persons for testing and benchmarking

set -e

echo "🌱 MongoDB Database Seeding Script"
echo "=================================="
echo ""

# Check if MongoDB is running
if ! docker ps | grep -q mongodb; then
    echo "❌ MongoDB container is not running"
    echo "   Starting MongoDB..."
    docker-compose up -d
    echo "   Waiting for MongoDB to be ready..."
    sleep 5
fi

# Check if OPENAI_API_KEY is set (optional for seeding)
if [ -z "$OPENAI_API_KEY" ]; then
    echo "⚠️  OPENAI_API_KEY not set (not required for seeding)"
fi

# Default to 10,000 records, or use environment variable
SEED_COUNT=${SEED_COUNT:-10000}

echo "📊 Configuration:"
echo "   Records to seed: $SEED_COUNT"
echo ""

echo "🚀 Starting seed process..."
echo ""

# Run application with seed profile
./gradlew bootRun --args='--spring.profiles.active=seed' -Dorg.gradle.jvmargs="-DSEED_COUNT=$SEED_COUNT"

echo ""
echo "✅ Seeding complete!"
echo ""
echo "📈 Next steps:"
echo "   1. Run the application: ./gradlew bootRun"
echo "   2. Test nearby search with 10k records"
echo "   3. Benchmark query performance"
