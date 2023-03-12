#!/usr/bin/env bash

req() {
  if [[ $3 != "" ]]; then
    response=$(curl -s -w "\n%{http_code}" -X $1 $4 -H "$2" --data-raw $3)
  else
    response=$(curl -s -w "\n%{http_code}" -X $1 $4 -H "$2")
  fi

  http_code=$(tail -n1 <<< "$response")
  content=$(sed '$ d' <<< "$response")

  echo "$http_code|$content"
}

_status() {
  echo $(cut -d "|" -f 1 <<< $1)
}

_content() {
  echo $(cut -d "|" -f 2 <<< $1)
}


login(){
  data='{"username":"'$1'","password":"admin"}'
  response=$(req "POST" "Content-Type:application/json" $data 'http://localhost:8899/auth/login')
  status=$(_status $response)
  content=$(_content $response)
  echo "<Login>"
  echo "Status: $status"
  if [[ "$content" != "" ]]; then
    echo "Content: $content"
  fi
  echo "</Login>"
}

me() {
  echo "<Login>"
  data='{"username":"'$1'","password":"admin"}'
  response=$(req "POST" "Content-Type:application/json" $data 'http://localhost:8899/auth/login')

  status=$(_status $response)
  content=$(_content $response)
  echo "Status: $status"
  if [[ "$content" != "" ]]; then
    echo "Content: $content"
  fi
  echo "</Login>"

  if [[ $status == "200" ]]; then
    access_token=$(echo $content | jq -r '.access_token')
    header="Authorization: Bearer $access_token"
    me_response=$(req "GET" "${header}" "" "http://localhost:8899/auth/me")
    http_code=$(_status $me_response)
    content=$(_content $me_response)
    echo "<me>"
    echo "Status: $http_code"
    echo "Content: $content"
    echo "</me>"
  fi
}

logout() {
    echo "<Login>"
    response=$(curl -s -w "\n%{http_code}"  http://localhost:8899/auth/login -H "Content-Type: application/json" --data-raw '{"username":"'$1'","password":"admin"}')
  #  echo "$response"
    http_code=$(tail -n1 <<< "$response")  # get the last line
    content=$(sed '$ d' <<< "$response")   # get all but the last line which contains the status code
    echo "Status: $http_code"
    if [[ "$content" != "" ]]; then
      echo "Content: $content"
    fi
    echo "</Login>"

    if [[ $http_code == 200 ]]; then
      access_token=$(echo $content | jq -r '.access_token')
      logout_response=$(curl -s -w "\n%{http_code}" -X POST http://localhost:8899/auth/logout -H "Authorization: Bearer "$access_token)

      http_code=$(tail -n1 <<< "$logout_response")
      content=$(sed '$ d' <<< "$logout_response")
      echo "<Logout>"
      echo "Status: $http_code"
      echo "Content: $content"
      echo "</logout>"

      echo "<me>"
      if [[ $http_code == 200 ]]; then
        me_response=$(curl -s -w "\n%{http_code}" http://localhost:8899/auth/me -H "Authorization: Bearer "$access_token)

        http_code=$(tail -n1 <<< "$me_response")
        content=$(sed '$ d' <<< "$me_response")

        echo "Status: $http_code"
        echo "Content: $content"

      fi
      echo "</me>"

    fi
}

main() {
  while true; do
    case "$1" in
      login )
        shift 1;
        login $*;
        break;;
      me )
        shift 1
        me $*;
        break;;
      logout )
        shift 1
        logout $*;
        break;;
   esac
  done
}

main "$@"
