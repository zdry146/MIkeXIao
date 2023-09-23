#!/bin/bash
#Test script for web service

curl -s  -d '{ "username": "alice" }' -X POST -H "Content-Type: application/json" http://localhost:4567/users

curl -s http://localhost:4567/users/alice/profile | jq


curl -s -d '{ "username": "bob" }' -X POST -H "Content-Type: application/json" http://localhost:4567/users
curl -s -d '{ "username": "eve" }' -X POST -H "Content-Type: application/json" http://localhost:4567/users
curl -s -d '{ "username": "sandy" }' -X POST -H "Content-Type: application/json" http://localhost:4567/users


curl -s -X PUT http://localhost:4567/users/alice/followers/bob | jq
curl -s -X PUT http://localhost:4567/users/alice/followers/eve | jq

curl -s http://localhost:4567/users/alice/followers | jq

curl -s http://localhost:4567/users/bob/following | jq

#Test Delete
curl -s -X PUT http://localhost:4567/users/alice/followers/sandy | jq
curl -s http://localhost:4567/users/alice/followers | jq
curl -s -X DELETE http://localhost:4567/users/alice/followers/sandy | jq
curl -s http://localhost:4567/users/alice/followers | jq

curl -s http://localhost:4567/users/alice/followers_count | jq

curl -s http://localhost:4567/users/bob/following_count | jq

curl -s  -d '{ "text": "I am telling you about this thing" }' -X POST -H "Content-Type: application/json" http://localhost:4567/users/alice/posts
curl -s  http://localhost:4567/users/alice/posts | jq 

curl -s  http://localhost:4567/users/bob/feed | jq 
