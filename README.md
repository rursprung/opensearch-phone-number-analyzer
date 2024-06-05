# Phone Number Analyzer & Tokenizer for OpenSearch

This plugin provides an analyzer & tokenizer for fields which contain phone numbers, supporting a variety of formats
(with/without international calling code, different country formats, etc.).

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
