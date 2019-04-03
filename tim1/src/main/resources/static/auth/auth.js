function getJwtToken(token_key){
	return localStorage.getItem(token_key);
}

function setJwtToken(token_key, token){
	localStorage.setItem(token_key, token);
}

function removeJwtToken(token_key){
	localStorage.removeItem(token_key);
}

function createAuthorizationTokenHeader(token_key){
	var token = getJwtToken(token_key)
	
	if(token){
		return {
            "Authorization": "Bearer " + token,
            'Content-Type': 'application/json'
          };
	} else{
		return {
            'Content-Type': 'application/json'
          };
	}
}