# This file contains your application, it requires dependencies and necessary
# parts of the application.
require 'rubygems'
require 'ramaze'
require 'data_mapper'

# Make sure that Ramaze knows where you are
Ramaze.options.roots = [__DIR__]

require __DIR__('model/main_models')
require __DIR__('controller/init')


DataMapper.setup(:default, 'postgres://dc:dc1234@localhost/dc_devel?encoding=UTF-8')
DataMapper.repository(:default).adapter.resource_naming_convention = DataMapper::NamingConventions::Resource::UnderscoredAndPluralizedWithoutModule
DataMapper.auto_upgrade!
