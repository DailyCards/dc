class CreateCarriers < ActiveRecord::Migration
  def change
    create_table :carriers do |t|
      t.string :name
      t.string :phoneno
      t.string :description

      t.timestamps
    end
  end
end
