class Campaign < ActiveRecord::Base
  belongs_to :carrier
  has_many :prizes
end
