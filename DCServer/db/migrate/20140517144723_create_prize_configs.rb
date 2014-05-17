class CreatePrizeConfigs < ActiveRecord::Migration
  def change
    create_table :prize_configs do |t|
      t.reference :campaign
      t.string :name
      t.string :description
      t.integer :amount

      t.timestamps
    end
  end
end
