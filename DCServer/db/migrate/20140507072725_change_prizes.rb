class ChangePrizes < ActiveRecord::Migration
  def change
     add_reference :prizes, :campaign, index: true
     remove_reference :prizes, :carrier
  end
end
