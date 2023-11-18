package pl.noteally.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestNameService {

    @Autowired
    private RestTemplate restTemplate;

    public boolean isNamePolish(String name)
    {
        Boolean response = restTemplate.getForObject("http://localhost:9090/" + name,Boolean.class);
        if(response == null) response = false;
        return response;
    }
}
