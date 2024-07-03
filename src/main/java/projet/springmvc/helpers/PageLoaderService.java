package projet.springmvc.helpers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PageLoaderService {

    private final RestTemplate restTemplate;

    public String loadPageContent(String url) {
        // Effectuer une requête HTTP GET vers l'URL de la page
        return restTemplate.getForObject(url, String.class);
    }
}