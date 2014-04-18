# Be sure to restart your server when you modify this file.

# Your secret key is used for verifying the integrity of signed cookies.
# If you change this key, all old signed cookies will become invalid!

# Make sure the secret is at least 30 characters and all random,
# no regular words or you'll be exposed to dictionary attacks.
# You can use `rake secret` to generate a secure secret key.

# Make sure your secret_key_base is kept private
# if you're sharing your code publicly.
DCServer::Application.config.secret_key_base = '7b54a2a7466031ea377c8d1475f3004b65d0efa007d1d348f6f862a6fa759bbff72d582c606b1dac59393bb5262bff4218f436c9c8cfeef4d501decb5351aebd'
