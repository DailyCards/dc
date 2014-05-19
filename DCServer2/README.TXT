1. How to Install 

  install jruby 

  jruby -S gem install bundler

  cd DCServer2

  jruby -S bundle install

2.Install postgresql server
  check if pgadmin3 is installed if not install it 
  start server 
  create user dc with password dc123
  create database dc_devel

3. How to Run 

  jruby start.rb
