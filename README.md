# Phone Number Analyzer & Tokenizer for OpenSearch

This plugin provides an analyzer & tokenizer for fields which contain phone numbers, supporting a variety of formats
(with/without international calling code, different country formats, etc.).

## Usage
You can use the `phone` and `phone-search` analyzers on your fields to index phone numbers. Use `phone` (which creates
ngrams) for the `analyzer` and `phone-search` (which doesn't create ngrams) for the `search_analyzer`.

You optionally can specify a region with the `phone-region` setting for the phone number which will ensure that phone
numbers without the international  dialling prefix (using `+`) are also tokenized correctly.

As an example for an index definition which defines the region:
```json
{
  "settings": {
    "analysis": {
      "analyzer": {
        "phone-ch": {
          "type": "phone",
          "phone-region": "CH"
        },
        "phone-search-ch": {
          "type": "phone-search",
          "phone-region": "CH"
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "phoneNumber": {
        "type": "text",
        "analyzer": "phone-ch",
        "search_analyzer": "phone-search-ch"
      }
    }
  }
}
```

## Prior Art
This is largely based on [elasticsearch-phone] and internally uses [libphonenumber].
This intentionally only ports a subset of the features: only `phone` is supported right now, `phone-email` and
`phone-search` can be added  if/when there's a clear need for it.

## License
This code is licensed under the Apache 2.0 License. See [LICENSE.txt](LICENSE.txt).

## Copyright
Copyright OpenSearch Contributors. See [NOTICE](NOTICE.txt) for details.

[elasticsearch-phone]: https://github.com/purecloudlabs/elasticsearch-phone
[libphonenumber]: https://github.com/google/libphonenumber
