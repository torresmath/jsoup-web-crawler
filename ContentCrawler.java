public class ContentCrawler {
   public Metadata getContent(String url) {

        private static final String MY_DEFAULT_IMAGE = "my_default_image.png";
        
        Metadata metadata = new Metadata();
        
        Try<Metadata> crawler = Try.of(() -> {
            /*
             * Parse an URL and get its HTML structure
             */
            Document document = Jsoup.connect(url).get();

            /*
             * Pick the first Element from parsed HTML based on .select filter
             *  then map the Element and return the absolute content from the HTML tag
             */
            Optional<String> image = Optional.ofNullable(document.head()
                    .select("meta[property~=og:image][content~=(?i)\\.(gif|png|jpe?g)]").first())
                    .map(element -> element.absUrl("content"));

            Optional<String> firstImage = Optional.ofNullable(document
                    .select("img[src~=(?i)\\.(gif|png|jpe?g)]:not([alt~=.svg|.png|Logo|logo])").first())
                    .map(element -> element.absUrl("src"));

            Optional<String> favicon = Optional.ofNullable(document.head()
                    .select("link[rel=shortcut icon][href~=(?i)\\.(ico|jpe?g|png)]").first())
                    .map(element -> element.absUrl("href"));

            Optional<String> favicon1 = Optional.ofNullable(document.head()
                    .select("link[type=image/x-icon][href~=(?i)\\.(ico|jpe?g|png)]").first())
                    .map(element -> element.absUrl("href"));

            Optional<String> text = Optional.ofNullable(document.head()
                    .select("meta[name=description]").first())
                    .map(element -> element.attr("content"));

            /*
             * Try to get image, if null get firstImage, if null get favicon. if null get default
             */
            String icon = image.orElseGet(() -> {
                String fav1 = favicon1.orElse(MY_DEFAULT_IMAGE);
                String fav = favicon.orElse(fav1);

                return firstImage.orElse(fav);
            });

            String description = text.orElse("");
            String title = document.title();

            MetadataField fields = MetadataField.builder().build();

            fields.setTitle(title);
            fields.setDescription(description);
            fields.setImage(icon);

            List<MetadataField> fields  = Collections.singletonList(fields);

            metadata.setFields(postFields);

            return metadata;
        });

        if (crawler.isFailure()) {
            return postMetadata;
        }

        return crawler.get();
    }

}
