#!/bin/sh
set -e

# Start background instance to seed initial data
redis-server --daemonize yes --protected-mode no --bind 0.0.0.0

until redis-cli ping > /dev/null 2>&1; do
  sleep 0.1
done

if [ "$(redis-cli exists user:1)" -eq 0 ]; then
  echo "Seeding Redis dummy users from /docker-data/users..."
  for f in /docker-data/users/*.json; do
    [ -e "$f" ] || continue
    id=$(basename "$f" .json)
    redis-cli -x set "user:$id" < "$f" > /dev/null
    redis-cli rpush user_ids "$id" > /dev/null
  done
  redis-cli save > /dev/null
  echo "Seeding completed: 10 users loaded into Redis."
fi

redis-cli shutdown
exec redis-server --protected-mode no --bind 0.0.0.0
